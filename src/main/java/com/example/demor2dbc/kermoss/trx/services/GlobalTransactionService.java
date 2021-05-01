package com.example.demor2dbc.kermoss.trx.services;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition.CompensateWhen;
import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition.ReceivedCommand;
import com.example.demor2dbc.kermoss.cache.BubbleCache;
import com.example.demor2dbc.kermoss.cache.BubbleMessage;
import com.example.demor2dbc.kermoss.entities.GlobalTransactionStatus;
import com.example.demor2dbc.kermoss.entities.WmGlobalTransaction;
import com.example.demor2dbc.kermoss.entities.WmInboundCommand;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;
import com.example.demor2dbc.kermoss.service.BusinessFlow;
import com.example.demor2dbc.kermoss.trx.message.CommitGtx;
import com.example.demor2dbc.kermoss.trx.message.RollBackGtx;
import com.example.demor2dbc.kermoss.trx.message.StartGtx;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GlobalTransactionService {

	@Autowired
	private R2dbcEntityTemplate template;
	@Autowired
	private BusinessFlow businessFlow;

	@Autowired
	BubbleCache bubbleCache;
	@Autowired
	private ReactiveTransactionManager tm;
	@Autowired
	private ObjectMapper objectMapper;

	@EventListener
	public Mono<Void> begin(StartGtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);

		WmGlobalTransaction wmg = new WmGlobalTransaction();
		wmg.setStatus(GlobalTransactionStatus.STARTED);
		wmg.setName(event.getPipeline().getMeta().getTransactionName());
		wmg.setId(UUID.randomUUID().toString());
//TODOx (je pense pas que l'idempotence est necessaire , voir une autre fois) vérifier que l'idemptence si l'event se produit plusieur fois par le scheduler par parent gtx
		// TODOx séparer pipeToMONO (inner/outer)
		return Mono.just(wmg).flatMap(e -> bubbleCache.getBubble(event.getPipeline().getIn().getId()).map(bm -> {
			//TODOx Not yet tested case of global commit or rollback blow (event or compensate) attach gtx as parent for new global tx 
			e.setParent((bm.getGLTX()!=null && bm.getPGTX()==null)?bm.getGLTX():bm.getPGTX());
			return e;
		})).then(template.insert(wmg)).then(pipeToMono(wmg, event.getPipeline()))
				.thenEmpty(businessFlow.consumeSafeEvent(event.getPipeline().getIn()).then())
				.onErrorResume(x->compensate(x, wmg, event.getPipeline()))
				.as(rxtx::transactional);
	}

	Mono<Void> pipeToMono(WmGlobalTransaction wmg, GlobalTransactionStepDefinition pipeline) {
		Mono<Void> mono = Mono.empty();
		// add Transaction context in buubleMessage
		Mono<BubbleMessage> bubbleMessage = BuildBubbleMessage(wmg);

		ReceivedCommand receivedCommad = pipeline.getReceivedCommad();
		if (receivedCommad != null) {
			Mono<Void> receivedCommadMono = bubbleCache.getBubble(pipeline.getIn().getId())
					.flatMap(bm -> template
							.selectOne(Query.query(Criteria.where("id").is(bm.getCommandId())), WmInboundCommand.class)
							.map(cmd -> {

								try {
									return objectMapper.readValue(cmd.getPayload(), receivedCommad.getTarget());
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
								return null;

							}))
					.flatMap(target -> (Mono<Void>) receivedCommad.getConsumer().apply(target));
			mono = mono.then(receivedCommadMono);

		}

		// let blow at end
		Stream<BaseTransactionEvent> blow = pipeline.getBlow();
		if (blow != null) {
			Mono<Void> blowFlux = Flux.fromStream(blow).concatMap(
					e -> bubbleCache.getOrAddBubble(e.getId(), bubbleMessage).then(businessFlow.publishSafeEvent(e)))
					.then();
			mono = mono.then(blowFlux);
		}

		return mono;
	}
   
	// global propagation not yet treated
	Mono<Void> compensate(Throwable x, WmGlobalTransaction wmg, GlobalTransactionStepDefinition pipeline) {
		Mono<BubbleMessage> bubbleMessage = BuildBubbleMessage(wmg);
		CompensateWhen compensateWhen = pipeline.getCompensateWhen();
		if (compensateWhen != null) {
			Stream<Class<Exception>> exs = Stream.of(compensateWhen.getExceptions());
			if (exs.anyMatch(ex -> ex.isAssignableFrom(x.getClass()))) {
				Flux<BaseTransactionEvent> cpWhenblow = (Flux<BaseTransactionEvent>) compensateWhen.getBlow();

				return cpWhenblow.concatMap(evt -> bubbleCache.getOrAddBubble(evt.getId(), bubbleMessage)
						.then(businessFlow.publishSafeEvent(evt))).then();
			}
		}

		return Mono.error(x);

	}

	Mono<BubbleMessage> BuildBubbleMessage(WmGlobalTransaction wmg) {
		return Mono.just(BubbleMessage.builder().GLTX(wmg.getId()).build());
	}

	public Mono<WmGlobalTransaction> findGtx(String gtx) {
		return template.select(WmGlobalTransaction.class).matching(Query.query(Criteria.where("id").is(gtx))).one()
				.switchIfEmpty(Mono.error(() -> new RuntimeException("Cannot find GTX")));
	}

	@EventListener
	public Mono<Void> commit(CommitGtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);

		Mono<BubbleMessage> bubbleMessage = bubbleCache.getBubble(event.getPipeline().getIn().getId()).switchIfEmpty(
				Mono.error(() -> new RuntimeException("this event is not linked to any transaction context")));

		return bubbleMessage.

				flatMap(bm -> findGtx(bm.getGLTX())).flatMap(wmg -> {
					if (wmg.getStatus().equals(GlobalTransactionStatus.COMITTED)) {
						// split to inner and outer
						return pipeToMono(wmg, event.getPipeline()).
								onErrorResume(x->compensate(x, wmg, event.getPipeline()));
					} else if (wmg.getStatus().equals(GlobalTransactionStatus.STARTED)) {
						wmg.setStatus(GlobalTransactionStatus.COMITTED);
						return template.update(wmg).then(pipeToMono(wmg, event.getPipeline())).
								onErrorResume(x->compensate(x, wmg, event.getPipeline()));
					} else {
						return Mono.error(new RuntimeException("Cannot commit a rollbacked Transaction"));
					}
				}).thenEmpty(businessFlow.consumeSafeEvent(event.getPipeline().getIn()).then())
				.as(rxtx::transactional);
	}

	@EventListener
	public Mono<Void> rollback(RollBackGtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);

		Mono<BubbleMessage> bubbleMessage = bubbleCache.getBubble(event.getPipeline().getIn().getId()).switchIfEmpty(
				Mono.error(() -> new RuntimeException("this event is not linked to any transaction context")));

		return bubbleMessage.

				flatMap(bm -> findGtx(bm.getGLTX())).flatMap(wmg -> {
					if (wmg.getStatus().equals(GlobalTransactionStatus.ROLLBACKED)) {
						// split to inner and outer
						return pipeToMono(wmg, event.getPipeline())
								.onErrorResume(x->compensate(x, wmg, event.getPipeline()));
					} else if (wmg.getStatus().equals(GlobalTransactionStatus.STARTED)) {
						wmg.setStatus(GlobalTransactionStatus.ROLLBACKED);
						return template.update(wmg).then(pipeToMono(wmg, event.getPipeline())).
								onErrorResume(x->compensate(x, wmg, event.getPipeline()));
					} else {
						return Mono.error(new RuntimeException("Cannot rollback a commited Transaction"));
					}
				}).thenEmpty(businessFlow.consumeSafeEvent(event.getPipeline().getIn()).then()).as(rxtx::transactional);
	}

}
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
import com.example.demor2dbc.kermoss.cache.BubbleCache;
import com.example.demor2dbc.kermoss.cache.BubbleMessage;
import com.example.demor2dbc.kermoss.entities.GlobalTransactionStatus;
import com.example.demor2dbc.kermoss.entities.WmGlobalTransaction;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;
import com.example.demor2dbc.kermoss.service.BusinessFlow;
import com.example.demor2dbc.kermoss.trx.message.CommitGtx;
import com.example.demor2dbc.kermoss.trx.message.StartGtx;

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
	
	@EventListener
	public Mono<Void> begin(StartGtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		
		WmGlobalTransaction wmg = new WmGlobalTransaction();
		wmg.setStatus(GlobalTransactionStatus.STARTED);
		wmg.setName(event.getPipeline().getMeta().getTransactionName());
		wmg.setId(UUID.randomUUID().toString());

		return template.insert(wmg)
				.then(pipeToMono(wmg, event.getPipeline())).
				as(rxtx::transactional);
	}

	Mono<Void> pipeToMono(WmGlobalTransaction wmg, GlobalTransactionStepDefinition pipeline) {
		Mono<Void> mono = Mono.just(pipeline.getIn()).then();
		// add Transaction context in buubleMessage
		Mono<BubbleMessage> bubbleMessage = BuildBubbleMessage(wmg);
		Stream<BaseTransactionEvent> blow = pipeline.getBlow();
		if (blow != null) {
			Mono<Void> blowFlux = Flux.fromStream(blow).concatMap(
					e -> bubbleCache.getOrAddBubble(e.getId(), bubbleMessage).then(businessFlow.publishSafeEvent(e)))
					.then();
			mono = mono.then(blowFlux);
		}
		return mono;
	}

	Mono<BubbleMessage> BuildBubbleMessage(WmGlobalTransaction wmg) {
		return Mono.just(BubbleMessage.builder().GLTX(wmg.getId()).build());
	}

	public Mono<WmGlobalTransaction> findGtx(String gtx) {
		return template.select(WmGlobalTransaction.class).matching(Query.query(Criteria.where("id").is(gtx))).
				one().switchIfEmpty(Mono.error(() -> new RuntimeException("Cannot find GTX")));
	}

	@EventListener
	public Mono<Void> commit(CommitGtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		
		Mono<BubbleMessage> bubbleMessage = bubbleCache.getBubble(event.getPipeline().getIn().getId())
				.switchIfEmpty(Mono.error(() -> new RuntimeException("this event is not linked to any transaction context")));
		
		return bubbleMessage.
				
				flatMap(bm -> findGtx(bm.getGLTX())).flatMap(wmg->{
			if(wmg.getStatus().equals(GlobalTransactionStatus.COMITTED)) {
				return pipeToMono(wmg,event.getPipeline());
			}else {
				wmg.setStatus(GlobalTransactionStatus.COMITTED);
				return template.update(wmg).then(pipeToMono(wmg,event.getPipeline()));
			}
		}).as(rxtx::transactional);
	}

}
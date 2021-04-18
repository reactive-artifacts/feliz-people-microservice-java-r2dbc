package com.example.demor2dbc.kermoss.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.example.demor2dbc.kermoss.cache.BubbleCache;
import com.example.demor2dbc.kermoss.cache.BubbleMessage;
import com.example.demor2dbc.kermoss.entities.CommandMeta;
import com.example.demor2dbc.kermoss.entities.WmInboundCommand;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;
import com.example.demor2dbc.kermoss.events.InboundCommandStarted;
import com.example.demor2dbc.kermoss.service.BusinessFlow;

import reactor.core.publisher.Mono;

@Component
public class DefaultCommandTranslator {
	@Autowired
	private R2dbcEntityTemplate template;
	
	@Autowired
	private BubbleCache bubbleCache;
	
	@Autowired
	private BusinessFlow businessFlow;

	@Autowired
	private DecoderRegistry decoders;
	
	@Autowired
	private ReactiveTransactionManager tm;

	@EventListener
	public Mono<Void> onEvent(final InboundCommandStarted commandStarted) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		final CommandMeta meta = commandStarted.getMeta();
		final String subject = meta.getSubject();
		if (decoders.containsKey(subject)) {
			final BaseDecoder decoder = decoders.get(subject);
			final BaseTransactionEvent event = decoder.decode(meta);
			Mono<BubbleMessage> bubbleMessage = BuildBubbleMessage(meta);
			
			return findInBoundCommand(meta.getCommandId()).flatMap(cmd -> {
				cmd.changeStatusToCompleted();
				return template.update(cmd);
			}).then(bubbleCache.getOrAddBubble(event.getId(), bubbleMessage))
			.then(businessFlow.publishSafeEvent(event)).then().
			as(rxtx::transactional);
		} else {
			throw new RuntimeException("No decoder Found For " + subject);
		}

	}

	public Mono<WmInboundCommand> findInBoundCommand(String id) {
		return template.select(WmInboundCommand.class).matching(Query.query(Criteria.where("id").is(id))).one()
				.switchIfEmpty(Mono.error(() -> new RuntimeException("Cannot find Inboud Command")));
	}

	Mono<BubbleMessage> BuildBubbleMessage(CommandMeta meta) {
		return Mono.just(BubbleMessage.builder().LTX(meta.getLTX()).GLTX(meta.getGTX()).FLTX(meta.getFLTX())
				.PGTX(meta.getPGTX()).trace(meta.getTraceId()).commande(meta.getCommandId()).build());
	}

}
package com.example.demor2dbc.kermoss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.example.demor2dbc.kermoss.bfm.BaseTransactionCommand;
import com.example.demor2dbc.kermoss.cache.BubbleCache;
import com.example.demor2dbc.kermoss.cache.BubbleMessage;
import com.example.demor2dbc.kermoss.entities.WmEvent;
import com.example.demor2dbc.kermoss.entities.WmGlobalTransaction;
import com.example.demor2dbc.kermoss.entities.WmInboundCommand;
import com.example.demor2dbc.kermoss.entities.WmOutboundCommand;
import com.example.demor2dbc.kermoss.entities.WmOutboundCommand.WmOutboundCommandBuilder;
import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;
import com.example.demor2dbc.kermoss.events.InboundCommandStarted;
import com.example.demor2dbc.kermoss.events.OutboundCommandStarted;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class BusinessFlow {
	@Autowired
	private ReactiveTransactionManager tm;
	
	@Autowired
	private R2dbcEntityTemplate template;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private BubbleCache bubbleCache; 

	@Autowired
	private ApplicationEventPublisher publisher;

	@Transactional(propagation = Propagation.MANDATORY)
	public Mono<BaseGlobalTransactionEvent> newGlobalTransaction(BaseGlobalTransactionEvent event) {
		return publishSafeEvent(event).cast(BaseGlobalTransactionEvent.class);
	}
    
	public Mono<BaseTransactionEvent> publishSafeEvent(BaseTransactionEvent event){
		WmEvent wmEvent = new WmEvent();
		wmEvent.setId(event.getId());
		wmEvent.setName(event.getClass().getName());
		try {
			wmEvent.setPayload(objectMapper.writeValueAsString(event));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return template.insert(wmEvent).flatMap(wme -> TransactionSynchronizationManager.forCurrentTransaction()
				.map(tm -> {
					tm.registerSynchronization(new TransactionSynchronization() {
						@Override
						public Mono<Void> afterCompletion(int status) {

							return Mono.just(event).publishOn(Schedulers.boundedElastic()).doOnNext(evt -> {
								publisher.publishEvent(evt);
							}).then();
						}

					});
					return event;
				}));
	}
	
	
	
	public Mono<Void> recieveOutBoundCommand(BaseTransactionCommand command){
		
		Mono<WmOutboundCommand> mCommand = commandMapper(command);
		
		return mCommand.flatMap(x->saveOutBoundCommand(x)).then();
	}
	
	private Mono<WmOutboundCommand> saveOutBoundCommand(WmOutboundCommand command){
		//check if exist before insert
		return template.insert(command).flatMap(wmc -> TransactionSynchronizationManager.forCurrentTransaction()
				.map(tm -> {
					tm.registerSynchronization(new TransactionSynchronization() {
						@Override
						public Mono<Void> afterCompletion(int status) {
                            
							return Mono.just(new OutboundCommandStarted(command.buildMeta())).publishOn(Schedulers.boundedElastic()).doOnNext(evt -> {
								publisher.publishEvent(evt);
							}).then();
						}

					});
					return command;
				}));
	}
	
	
   public Mono<Void> recieveInBoundCommand(WmInboundCommand command){  
	   TransactionalOperator rxtx = TransactionalOperator.create(tm);
	   return saveInBoundCommand(command).then().as(rxtx::transactional);
	}
	
	private Mono<WmInboundCommand> saveInBoundCommand(WmInboundCommand command){
		
		Mono<WmInboundCommand> mCommand=template.select(WmInboundCommand.class)
		.matching(Query.query(Criteria.where("ref_id").is(command.getRefId()))).one().
		switchIfEmpty(template.insert(command));
		return mCommand.flatMap(wmc -> TransactionSynchronizationManager.forCurrentTransaction()
				.map(tm -> {
					tm.registerSynchronization(new TransactionSynchronization() {
						@Override
						public Mono<Void> afterCompletion(int status) {
                            
							return Mono.just(new InboundCommandStarted(command.buildMeta())).publishOn(Schedulers.boundedElastic()).doOnNext(evt -> {
								publisher.publishEvent(evt);
							}).then();
						}

					});
					return command;
				}));
	}
	
	Mono<WmOutboundCommand> commandMapper(BaseTransactionCommand baseTransactionCommand){
        Mono<BubbleMessage> bubbleMessage = bubbleCache.getBubble(baseTransactionCommand.getId());
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(baseTransactionCommand.getPayload());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("objectMapper.writeValueAsString Exception");
        }
        WmOutboundCommandBuilder builder = WmOutboundCommand.builder()
        .additionalHeaders(baseTransactionCommand.getHeader())
        .payload(payload)
        .subject(baseTransactionCommand.getSubject())
        .destination(baseTransactionCommand.getDestination());
        
        return bubbleMessage.map(bm->builder
                .gTX(bm.getGLTX())
                .pGTX(bm.getPGTX())
                .fLTX(bm.getFLTX())
                .lTX(bm.getLTX())
                .trace(bm.getTraceId())
                .build());
    }
}
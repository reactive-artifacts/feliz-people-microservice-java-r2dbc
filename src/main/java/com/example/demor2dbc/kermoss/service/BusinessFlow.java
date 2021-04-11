package com.example.demor2dbc.kermoss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;

import com.example.demor2dbc.kermoss.entities.WmEvent;
import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class BusinessFlow {
	@Autowired
	private R2dbcEntityTemplate template;
	@Autowired
	private ObjectMapper objectMapper;

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
}
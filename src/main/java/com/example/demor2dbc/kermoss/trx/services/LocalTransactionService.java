package com.example.demor2dbc.kermoss.trx.services;

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
import com.example.demor2dbc.kermoss.entities.WmGlobalTransaction;
import com.example.demor2dbc.kermoss.service.BusinessFlow;
import com.example.demor2dbc.kermoss.trx.message.CommitLtx;
import com.example.demor2dbc.kermoss.trx.message.StartLtx;

import reactor.core.publisher.Mono;

@Component
public class LocalTransactionService {

	@Autowired
	private R2dbcEntityTemplate template;
	@Autowired
	private BusinessFlow businessFlow;

	@Autowired
	BubbleCache bubbleCache;
	@Autowired
	private ReactiveTransactionManager tm;
	
	@EventListener
	public Mono<Void> begin(StartLtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);		
	   return null;
	}

	

	Mono<BubbleMessage> BuildBubbleMessage(WmGlobalTransaction wmg) {
		return Mono.just(BubbleMessage.builder().GLTX(wmg.getId()).build());
	}

	public Mono<WmGlobalTransaction> findGtx(String gtx) {
		return template.select(WmGlobalTransaction.class).matching(Query.query(Criteria.where("id").is(gtx))).
				one().switchIfEmpty(Mono.error(() -> new RuntimeException("Cannot find GTX")));
	}

	@EventListener
	public Mono<Void> commit(CommitLtx event) {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		return null;
		
	}

}
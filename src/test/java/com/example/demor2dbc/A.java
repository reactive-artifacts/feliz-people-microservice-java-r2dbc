package com.example.demor2dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class A {
	@Autowired
	private ReactiveTransactionManager tm;
	
	@Autowired
	private B btest;
	
	public Flux<String> x() {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		Flux<String> as = Flux.just("a","b").flatMap(x->btest.getMono(x)).as(rxtx::transactional);
		
		return as.doOnError(x->System.out.println(x));
	}
	
	public Mono<Void> y() {
		return Flux.just("a","b").flatMap(x->btest.getMono(x)).then();
	}
	
}

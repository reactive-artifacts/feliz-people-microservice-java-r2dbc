package com.example.demor2dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class A {

	@Autowired
	private B btest;
	
	public Flux<String> x() {
		return Flux.just("a","b").flatMap(x->btest.getMono(x));
	}
	
	public Mono<Void> y() {
		return Flux.just("a","b").flatMap(x->btest.getMono(x)).then();
	}
	
}

package com.example.demor2dbc;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class B {
 
	Mono<String> getMono(String x){
	  return Mono.just(x).map(e->e.toUpperCase());	
	}
	
}

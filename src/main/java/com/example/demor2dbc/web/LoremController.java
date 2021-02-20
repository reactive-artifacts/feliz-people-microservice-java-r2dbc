package com.example.demor2dbc.web;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.exceptions.ResourceNotFoundException;
import com.example.demor2dbc.mappers.PersonMapper;
import com.example.demor2dbc.security.SecurityUtils;
import com.example.demor2dbc.security.UserDto;
import com.example.demor2dbc.web.dto.response.HrPersonDto;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/lorem")
//labo for testing reactor ...
public class LoremController {
	@GetMapping(value = "/X")
	public void indeX() {
		Flux<Long> fallback = Flux.interval(Duration.ofSeconds(2)).takeWhile(i->i<5).doOnNext(i -> System.out.println("in fall back"));
		Flux.interval(Duration.ofSeconds(1)).concatMap(i -> i < 10L ? Flux.just(i) : Flux.error(new RuntimeException()))
				.onErrorResume(ex -> fallback).subscribe(i -> System.out.println("ssssss" + i));
	}

	@GetMapping(value = "/Y")
	public Flux<Person> indeY() {
		Sinks.One<Integer> replaySink = Sinks.one();
		Mono<Integer> mono = replaySink.asMono();
		mono.subscribe(x -> System.out.println("xingo 1"));
		mono.subscribe(x -> System.out.println("xingo 2"));

//       Flux<Long> pFlux = Flux.interval(Duration.ofSeconds(1)).take(2).doOnNext(x->System.out.println("pFlux"+x)).
//    		   replay().refCount();

		// **** refCount reset replay history when source complete when new subscriber
		// subscribe , will only send to him complete signal
		// **** refcount reset replay history when no subscriber (refcount==0) and
		// create new source when new subscriber
		// replay().autoconnect() dn't reset replay history even source complete (it
		// send history+complete signal (if source completed))
		Flux<Long> pFlux = Flux.interval(Duration.ofSeconds(1)).takeUntilOther(mono)
				.doOnNext(x -> System.out.println("pFlux" + x)).replay().autoConnect();
		Mono<Long> xx = pFlux.take(5).reduce(0L, (acc, cur) -> acc + cur);
		Flux<Long> yy = pFlux.take(10).doOnNext(x -> System.out.println(x));
		Flux<Long> zz = pFlux.take(10).doOnNext(x -> System.out.println(x));
		Disposable subscribe = Flux.zip(yy, zz, xx).subscribe(x -> {
			replaySink.emitEmpty(null);
//		replaySink.emitValue(2, null);
			System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwww " + x);
		});

		CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {

			// replaySink.emitValue(1, null);

			pFlux.subscribe(x -> System.out.println("JJJJJJJJJJJJJJ " + x),
					error -> System.err.println("Error " + error), () -> System.out.println("JJJJJJJJJJJJJJ+Done"));
		});
		return null;
	}
	
	@GetMapping("/test-reactive-context")
	@ResponseStatus(HttpStatus.OK)
	public void context(Authentication authentication,@PathVariable("id") long id) {
         Mono<UserDto> currentUser = SecurityUtils.getCurrentUser();
        Flux<Long> takeWhile = Flux.interval(Duration.ofSeconds(1)).takeWhile(x->x<10).doOnNext(x->System.out.println("in take while"));
        Flux.zip(currentUser,takeWhile).
        contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)).
        subscribe(x -> System.out.println("JJJJJJJJJJJJJJ " + x),
				error -> System.err.println("Error " + error), () -> System.out.println("JJJJJJJJJJJJJJ+Done"));
	}
	
	
	@GetMapping("/combine-latest")
	@ResponseStatus(HttpStatus.OK)
	public void combine() {
		
        Flux<Long> fluxOflong = Flux.interval(Duration.ofSeconds(1)).takeWhile(x->x<10).doOnNext(x->System.out.println("in take while"));
	    Mono<String> justAString = Mono.just("string");
	    Flux.combineLatest(a->a[0].toString()+a[1].toString(),fluxOflong,justAString).subscribe(x->System.out.println(x));
	
	}

}
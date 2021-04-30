package com.example.demor2dbc.web;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.kermoss.cache.BubbleCache;
import com.example.demor2dbc.kermoss.cache.BubbleMessage;
import com.example.demor2dbc.kermoss.entities.GlobalTransactionStatus;
import com.example.demor2dbc.kermoss.entities.WmEvent;
import com.example.demor2dbc.kermoss.entities.WmGlobalTransaction;
import com.example.demor2dbc.kermoss.saga.remote.InvoiceGlobalTransactionEvent;
import com.example.demor2dbc.repositories.PersonReactRepo;
import com.example.demor2dbc.security.SecurityUtils;
import com.example.demor2dbc.security.UserDto;
import com.example.demor2dbc.web.dto.response.HrPersonDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping("/lorem")
//labo for testing reactor ...
//http://tutorials.jenkov.com/java-nio/selectors.html
//https://programmer.group/basic-concepts-of-netty.html

//https://github.com/pioardi/Spring-Reactive/tree/master/src/main/java/com/aardizio
//https://staging--okta-blog.netlify.app/blog/2018/09/24/reactive-apis-with-spring-webflux	

//https://docs.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/domain-events-design-implementation
// https://github.com/reactor/reactor-kafka/blob/master/reactor-kafka-samples/src/main/java/reactor/kafka/samples/SampleScenarios.java	
//https://akarnokd.blogspot.com/2016/03/operator-fusion-part-1.html
//https://github.com/spring-projects/spring-kafka/blob/master/spring-kafka/src/main/java/org/springframework/kafka/support/serializer/JsonSerializer.java
public class LoremController {

	@Autowired
	private BubbleCache bc;

	public static final Logger LOG = Loggers.getLogger(LoremController.class);
	@Autowired
	public PersonReactRepo prp;

	// private final Log logger = LogFactory.getLog(getClass());
	@GetMapping(value = "/X")
	public void indeX() {
		Flux<Long> fallback = Flux.interval(Duration.ofSeconds(2)).takeWhile(i -> i < 5)
				.doOnNext(i -> System.out.println("in fall back"));
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
	public void context(Authentication authentication, @PathVariable("id") long id) {
		Mono<UserDto> currentUser = SecurityUtils.getCurrentUser();
		Flux<Long> takeWhile = Flux.interval(Duration.ofSeconds(1)).takeWhile(x -> x < 10)
				.doOnNext(x -> System.out.println("in take while"));
		Flux.zip(currentUser, takeWhile).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
				.subscribe(x -> System.out.println("JJJJJJJJJJJJJJ " + x),
						error -> System.err.println("Error " + error), () -> System.out.println("JJJJJJJJJJJJJJ+Done"));
	}

	@GetMapping("/combine-latest")
	@ResponseStatus(HttpStatus.OK)
	public void combine() {

		Flux<Long> fluxOflong = Flux.interval(Duration.ofSeconds(1)).takeWhile(x -> x < 10)
				.doOnNext(x -> System.out.println("in take while"));
		Mono<String> justAString = Mono.just("string");
		Flux.combineLatest(a -> a[0].toString() + a[1].toString(), fluxOflong, justAString)
				.subscribe(x -> System.out.println(x));

	}

	@GetMapping("/merge")
	@ResponseStatus(HttpStatus.OK)
	public void merge() {

		Flux<Integer> range = Flux.range(1, 10).map(x -> {
			if (x == 5)
				throw new RuntimeException("erororor");
			return x;
		}).onErrorContinue((ex, x) -> System.out.println("continu on error" + x)).replay(1).autoConnect();

		Flux<Integer> autoConnect = Flux.merge(range)
				.onErrorContinue((ex, x) -> System.out.println("Tok Tok error" + x));
		Flux<Integer> autoConnect2 = Flux.merge(range);
		autoConnect.subscribe(x -> System.out.println("xxxx" + x), error -> System.out.println("error" + error),
				() -> System.out.println("done"));
		autoConnect2.subscribe(x -> System.out.println("yyyy" + x), error -> System.out.println("error2" + error),
				() -> System.out.println("done"));

		CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
			autoConnect.subscribe(x -> System.out.println("JJJJJJJJJJJJJJ " + x),
					error -> System.err.println("Error vvvv " + error),
					() -> System.out.println("JJJJJJJJJJJJJJ+Done"));
		});

	}

	@GetMapping("/create1")
	@ResponseStatus(HttpStatus.OK)
	public void create() {
		Flux<Object> fluxAsyncBackp = Flux.create(emitter -> {

			// Publish 1000 numbers
			for (int i = 0; i < 500; i++) {
				System.out.println(Thread.currentThread().getName() + " | Publishing = " + i);
				emitter.next(i);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			// When all values or emitted, call complete.
			// emitter.complete();

		}, OverflowStrategy.IGNORE).replay(1).autoConnect();

		fluxAsyncBackp.
		// onBackpressureLatest().
				onBackpressureBuffer(10, x -> System.out.println("buffer Overflow ellement=" + x),
						BufferOverflowStrategy.DROP_LATEST)
				.
				// onBackpressureBuffer(20,BufferOverflowStrategy.DROP_LATEST).
				onErrorContinue((ex, obj) -> System.out.println(ex + " For obj " + obj))
				.subscribeOn(Schedulers.elastic()).publishOn(Schedulers.elastic()).subscribe(i -> {
					// Process received value.
					System.out.println(Thread.currentThread().getName() + " | Received 1 = " + i);
					// 100 mills delay to simulate slow subscriber
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}, e -> {
					// Process error
					System.err.println(Thread.currentThread().getName() + " | Error = " + e.getMessage());
				}, () -> System.out.println("done"));

		fluxAsyncBackp.subscribeOn(Schedulers.elastic()).publishOn(Schedulers.elastic()).subscribe(i -> {
			// Process received value.
			System.out.println(Thread.currentThread().getName() + " | Received 2 = " + i);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		}, e -> {
			// Process error
			System.err.println(Thread.currentThread().getName() + " | Error 2 = " + e.getMessage());
		});

	}

	@GetMapping("/sh")
	@ResponseStatus(HttpStatus.OK)
	public Flux<Person> schedulers() {
		return Flux.range(1, 20)// .publishOn(Schedulers.parallel())
				.concatMap(x -> prp.findById(Long.valueOf(x)).subscribeOn(Schedulers.parallel()).doOnNext(e -> {
					// LOG.error("mmmmmm");
					System.out.println(e + "==" + Thread.currentThread().getName());

				}));

	}

	@GetMapping("/bc")
	@ResponseStatus(HttpStatus.OK)
	public void asBc() {
		String uuid = UUID.randomUUID().toString();
		Mono<String> just = Mono.just("5");
		just.flatMap(x -> bc.getBubble(x).doOnNext(e -> System.out.println("AAAAAAAA")))
				.switchIfEmpty(just.doOnNext(e -> System.out.println("BBBBBB"))
						.flatMap(x -> bc.getOrAddBubble(x, Mono.just(new BubbleMessage(uuid, "xcv")))))
				.subscribe(x -> System.out.println("JJJJJJJJJJJJJJ " + x.getGLTX()),
						error -> System.err.println("Error " + error), () -> System.out.println("JJJJJJJJJJJJJJ+Done"));
	}

	@Autowired
	private ReactiveTransactionManager tm;
	@Autowired
	private R2dbcEntityTemplate template;

	@GetMapping("/tx")
	@ResponseStatus(HttpStatus.OK)
	public void asTx() {
		// System.out.println(tm);
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		WmGlobalTransaction wmg = new WmGlobalTransaction();
		wmg.setStatus(GlobalTransactionStatus.STARTED);
		wmg.setName("xss6");
		wmg.setId(UUID.randomUUID().toString());
		WmEvent wmEvent = new WmEvent();
		wmEvent.setId(UUID.randomUUID().toString());
		wmEvent.setName("ev6");

		Mono<WmGlobalTransaction> as = template.insert(wmg).doOnNext(x -> {
			// throw new RuntimeException("VVVX");
		}).as(rxtx::transactional);
		Mono<WmEvent> as2 = template.insert(wmEvent).doOnNext(x -> {
			throw new RuntimeException("VVVX");

		}).as(rxtx::transactional);

		as2.subscribe();
		as.subscribe();

	}

	@Autowired
	private ReactorLoadBalancerExchangeFilterFunction lbFunction;

	@GetMapping("/wc")
	@ResponseStatus(HttpStatus.OK)
	public Mono<String> doOtherStuff() {
		return WebClient.builder().baseUrl("http://feliz-people").filter(lbFunction).build().get().uri("/lorem/stg")
				.retrieve().bodyToMono(String.class);
	}

	@GetMapping("/wcp")
	@ResponseStatus(HttpStatus.OK)
	public Mono<HttpStatus> doOtherPost() {
		HrPersonDto wmp = new HrPersonDto();
		wmp.setId(1L);
		wmp.setAddress("as cv cbb");

		return WebClient.builder().baseUrl("http://feliz-people").filter(lbFunction).build().post().uri("/lorem/stgp")
				.contentType(MediaType.APPLICATION_JSON).body(Mono.just(wmp), HrPersonDto.class)
				.exchangeToMono(res -> Mono.just(res.statusCode()));
	}

	@GetMapping("/stg")
	@ResponseStatus(HttpStatus.OK)
	public Mono<String> stg() {
		return Mono.just("this is string");
	}

	@PostMapping(path = "/stgp", consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public void stg(@RequestBody Mono<HrPersonDto> stg) {
		stg.subscribe(x -> System.out.println("EEEEEEEEE" + x.getAddress()));
	}

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping("/xsdz")
	@ResponseStatus(HttpStatus.OK)
	public void wsdz() {
		template.select(WmEvent.class)
				.matching(Query.query(Criteria.where("id").is("2656f4d0-f75e-4360-a602-b5299ff91361"))).one()
				.flatMap(e -> {
					try {
						return Mono.just(objectMapper.readValue(e.getPayload(), Class.forName(e.getName())));
					} catch (JsonProcessingException | ClassNotFoundException e1) {
						
						e1.printStackTrace();
					}
					return null;
				}).subscribe(x->publisher.publishEvent(x));
	}
	
	@EventListener
	public void x(InvoiceGlobalTransactionEvent e) {
		System.out.println(e);
	}

}
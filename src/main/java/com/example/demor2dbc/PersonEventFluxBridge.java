package com.example.demor2dbc;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.events.DomainEvent;
import com.example.demor2dbc.events.PersonEvent;
import com.example.demor2dbc.exceptions.IllegalAccessOperation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class PersonEventFluxBridge {
	public static final Logger LOG = Loggers.getLogger(PersonKafkaSenderService.class);
	
	@Autowired
	PersonService personService;
	
	private Flux<DomainEvent> flux;

	private Consumer<PersonEvent> bridge;

	@PostConstruct
	public void init() {
		Flux<PersonEvent> personEventFlux = Flux.create(sink -> {
			bridge = (PersonEvent event) -> sink.next(event);
		},OverflowStrategy.IGNORE);
		
		flux = personEventFlux.
				flatMap(x->personService.findPerson(x.getPersonId()).
						map(e->x.enrich(e))).cast(DomainEvent.class)
				.onErrorContinue((ex,object)->{
		    		LOG.error("connot process event: "+object.toString(),ex);
		    	}).replay(1).autoConnect();
	}


	public Flux<DomainEvent> flux() {
		return flux;
	}

	public Consumer<PersonEvent> bridge() {
		if (bridge == null) {
			throw new IllegalAccessOperation("the flux must have at least one subscriber to become created");
		}
		;
		return bridge;
	}
}
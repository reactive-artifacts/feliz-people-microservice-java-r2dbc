package com.example.demor2dbc.kermoss.infra;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.events.DomainEvent;
import com.example.demor2dbc.events.PersonEvent;
import com.example.demor2dbc.exceptions.IllegalAccessOperation;
import com.example.demor2dbc.kermoss.entities.WmOutboundCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class OutBoundCommandFlux {
	public static final Logger LOG = Loggers.getLogger(OutBoundCommandFlux.class);
	
	
	private Flux<TransporterCommand> flux;

	private Consumer<TransporterCommand> bridge;

	@PostConstruct
	public void init() {
		Flux<TransporterCommand> outBoundCmdFlux = Flux.create(sink -> {
			bridge = (TransporterCommand event) -> sink.next(event);
		},OverflowStrategy.IGNORE);
		
		flux = outBoundCmdFlux.
				onErrorContinue((ex,object)->{
		    		LOG.error("connot process event: "+object.toString(),ex);
		    	}).replay(1).autoConnect();
	}


	public Flux<TransporterCommand> flux() {
		return flux;
	}

	public Consumer<TransporterCommand> bridge() {
		if (bridge == null) {
			throw new IllegalAccessOperation("the flux must have at least one subscriber to become created");
		}
		;
		return bridge;
	}
}
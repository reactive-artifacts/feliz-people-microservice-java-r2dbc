package com.example.demor2dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.events.PersonCreatedEvent;

import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class PersonEventListener {
	public static final Logger LOG = Loggers.getLogger(PersonEventListener.class);

	@Autowired
	PersonEventFluxBridge personBridge;

	@EventListener
	public void handlePersonCreatedEvent(PersonCreatedEvent pce) {

		personBridge.bridge().accept(pce);
	}
}
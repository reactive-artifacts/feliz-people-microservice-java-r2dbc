package com.example.demor2dbc.events;

import com.example.demor2dbc.entities.read.Person;

public class PersonUpdatedEvent extends PersonEvent {

	public PersonUpdatedEvent(Long personId) {
		super(personId);
	}

	@Override
	public PersonEvent enrich(Person person) {
		return null;
	}
	
}

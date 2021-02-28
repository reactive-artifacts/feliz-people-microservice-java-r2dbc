package com.example.demor2dbc.events;

import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.entities.write.WmPerson;

public class PersonCreatedEvent extends PersonEvent {
	
	private Person person;
	
	public PersonCreatedEvent(Long personId, Person person) {
		super(personId);
		this.person = person;
	}

	public PersonCreatedEvent(Long personId) {
		super(personId);
	}
	
	
	public static PersonCreatedEvent of(WmPerson wmp) {
		return new PersonCreatedEvent(wmp.getId());
	}

	public Person getPerson() {
		return person;
	}

	

	@Override
	public PersonEvent enrich(Person person) {
		return new PersonCreatedEvent(getPersonId(), person);
	}

}
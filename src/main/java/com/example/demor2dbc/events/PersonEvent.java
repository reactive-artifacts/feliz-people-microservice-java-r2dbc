package com.example.demor2dbc.events;

import java.time.LocalDate;

import com.example.demor2dbc.entities.read.Person;

public abstract class PersonEvent extends DomainEvent<Long> {
	private String topic = "person-events-"+LocalDate.now();
	private Long personId;

	public PersonEvent(Long personId) {
		super();
		this.personId = personId;
	}

	@Override
	public String topic() {
		return topic;
	}

	public abstract PersonEvent enrich(Person person);

	public Long getPersonId() {
		return personId;
	}
	@Override
	public Long key() {
		return personId;
	}
}
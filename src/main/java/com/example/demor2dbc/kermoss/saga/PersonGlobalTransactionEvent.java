package com.example.demor2dbc.kermoss.saga;

import com.example.demor2dbc.entities.write.WmPerson;
import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.fasterxml.jackson.annotation.JsonCreator;

public class PersonGlobalTransactionEvent extends BaseGlobalTransactionEvent {

    private final  WmPerson person;
    @JsonCreator
	public PersonGlobalTransactionEvent(WmPerson person) {
		super();
		this.person = person;
	}

	public WmPerson getPerson() {
		return person;
	}
	
	
     
     

}
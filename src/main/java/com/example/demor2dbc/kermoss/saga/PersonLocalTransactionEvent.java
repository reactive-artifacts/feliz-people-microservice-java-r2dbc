package com.example.demor2dbc.kermoss.saga;

import com.example.demor2dbc.entities.write.WmPerson;
import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;

public class PersonLocalTransactionEvent extends BaseLocalTransactionEvent {

    private final  WmPerson person;

	public PersonLocalTransactionEvent(WmPerson person) {
		super();
		this.person = person;
	}

	public WmPerson getPerson() {
		return person;
	}
	
	
     
     

}
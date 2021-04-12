package com.example.demor2dbc.kermoss.saga;

import com.example.demor2dbc.entities.write.WmPerson;
import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;

public class PersonNestedLocalTransactionEvent extends BaseLocalTransactionEvent {

    private final  WmPerson person;

	public PersonNestedLocalTransactionEvent(WmPerson person) {
		super();
		this.person = person;
	}

	public WmPerson getPerson() {
		return person;
	}
	
	
     
     

}
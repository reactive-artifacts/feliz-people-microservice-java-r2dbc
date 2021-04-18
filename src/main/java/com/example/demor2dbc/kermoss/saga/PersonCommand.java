package com.example.demor2dbc.kermoss.saga;

import com.example.demor2dbc.kermoss.bfm.BaseTransactionCommand;

public class PersonCommand extends BaseTransactionCommand {

	public PersonCommand() {
		super();
	}

	public PersonCommand(String subject, String header, Object payload, String destination) {
		super(subject, header, payload, destination);
	}
	
}
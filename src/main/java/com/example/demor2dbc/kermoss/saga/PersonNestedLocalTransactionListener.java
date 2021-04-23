package com.example.demor2dbc.kermoss.saga;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.LocalTransactionWorker;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;
import com.example.demor2dbc.kermoss.trx.annotation.BusinessLocalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.SwitchBusinessLocalTransactional;

@Component
public class PersonNestedLocalTransactionListener extends LocalTransactionWorker{
	
	public PersonNestedLocalTransactionListener() {
		super(new WorkerMeta("PersonNestedLocalTransactionService","PersonLocalTransactionService"));
	}



	@BusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonLtxEvent(PersonNestedLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitNestedLocalTransactionEvent(event.getPerson()))).
		meta(this.meta).build();
	}
	
	
	
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonCommitLtxEvent(PersonCommitNestedLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitLocalTransactionEvent(event.getPerson()))).
		meta(this.meta).build();
	}
	
}
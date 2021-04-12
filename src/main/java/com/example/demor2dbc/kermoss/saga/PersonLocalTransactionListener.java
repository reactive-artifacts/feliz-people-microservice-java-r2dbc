package com.example.demor2dbc.kermoss.saga;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;
import com.example.demor2dbc.kermoss.trx.annotation.BusinessLocalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.SwitchBusinessLocalTransactional;

@Component
public class PersonLocalTransactionListener {
	
	@BusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonLtxEvent(PersonLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonNestedLocalTransactionEvent(event.getPerson()))).
		meta(new WorkerMeta("PersonLocalTransactionService","PersonGlobalTransactionService")).build();
	}
	
	
	
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonCommitLtxEvent(PersonCommitLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitGlobalTransactionEvent(event.getPerson()))).
		meta(new WorkerMeta("PersonLocalTransactionService","PersonGlobalTransactionService")).build();
	}
	
}
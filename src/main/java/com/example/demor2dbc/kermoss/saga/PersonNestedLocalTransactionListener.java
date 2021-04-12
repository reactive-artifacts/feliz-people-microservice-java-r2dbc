package com.example.demor2dbc.kermoss.saga;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;
import com.example.demor2dbc.kermoss.trx.annotation.BusinessLocalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.SwitchBusinessLocalTransactional;

@Component
public class PersonNestedLocalTransactionListener {
	
	@BusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonLtxEvent(PersonNestedLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitNestedLocalTransactionEvent(event.getPerson()))).
		meta(new WorkerMeta("PersonNestedLocalTransactionService","PersonLocalTransactionService")).build();
	}
	
	
	
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonCommitLtxEvent(PersonCommitNestedLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitLocalTransactionEvent(event.getPerson()))).
		meta(new WorkerMeta("PersonNestedLocalTransactionService","PersonGlobalTransactionService")).build();
	}
	
}
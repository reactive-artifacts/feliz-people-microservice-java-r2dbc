package com.example.demor2dbc.kermoss.saga;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.example.demor2dbc.kermoss.trx.annotation.BusinessGlobalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.CommitBusinessGlobalTransactional;

@Component
public class PersonGlobalTransactionListener {
	
	@BusinessGlobalTransactional
	public GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> handlePersonCreatedEvent(PersonGlobalTransactionEvent event) {
		return GlobalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitGlobalTransactionEvent(event.getPerson()))).
		meta(new WorkerMeta("PersonGlobalTransactionService")).build();
	}
	
	
	@CommitBusinessGlobalTransactional
	public GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> handlecommit(PersonCommitGlobalTransactionEvent event) {
		return GlobalTransactionStepDefinition.builder().
		in(event).
		meta(new WorkerMeta("PersonGlobalTransactionService")).build();
	}
}
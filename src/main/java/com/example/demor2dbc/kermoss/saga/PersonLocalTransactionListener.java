package com.example.demor2dbc.kermoss.saga;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.LocalTransactionWorker;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;
import com.example.demor2dbc.kermoss.saga.domain.Invoice;
import com.example.demor2dbc.kermoss.trx.annotation.BusinessLocalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.SwitchBusinessLocalTransactional;

@Component
public class PersonLocalTransactionListener extends LocalTransactionWorker{
	
	public PersonLocalTransactionListener() {
		super(new WorkerMeta("PersonLocalTransactionService","PersonGlobalTransactionService"));
	}



	@BusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonLtxEvent(PersonLocalTransactionEvent event) {
		Invoice invoice= new Invoice(BigDecimal.valueOf(10.5), event.getPerson().getAddress());
		PersonCommand pc = new PersonCommand("pay-invoice", null, invoice, "feliz-people");
		return LocalTransactionStepDefinition.builder().
		in(event).
		send(Stream.of(pc)).
		blow(Stream.of(new PersonNestedLocalTransactionEvent(event.getPerson()))).
		meta(this.meta).build();
	}
	
	
	
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition<BaseLocalTransactionEvent> handlePersonCommitLtxEvent(PersonCommitLocalTransactionEvent event) {
		return LocalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new PersonCommitGlobalTransactionEvent(event.getPerson()))).
		meta(this.meta).build();
	}
	
}
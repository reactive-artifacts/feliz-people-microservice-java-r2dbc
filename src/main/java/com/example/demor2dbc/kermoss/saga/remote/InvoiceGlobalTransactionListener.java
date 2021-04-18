package com.example.demor2dbc.kermoss.saga.remote;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.example.demor2dbc.kermoss.saga.domain.Invoice;
import com.example.demor2dbc.kermoss.trx.annotation.BusinessGlobalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.CommitBusinessGlobalTransactional;
import com.example.demor2dbc.kermoss.trx.annotation.RollbackBusinessGlobalTransactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InvoiceGlobalTransactionListener {
	
	@BusinessGlobalTransactional
	public GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> handle(InvoiceGlobalTransactionEvent event) {
		return GlobalTransactionStepDefinition.builder().
		in(event).
		blow(Stream.of(new InvoiceCommitGlobalTransactionEvent())).
		receive(Invoice.class, x->{
			System.out.println("##################"+x);
			return Mono.just(x).flatMap(e->Mono.error(new RemoteException())).then();
			}).
		compensateWhen(Flux.just(new InvoiceRollbackGlobalTransactionEvent()),RemoteException.class)
		.meta(new WorkerMeta("InvoiceGlobalTransactionService")).build();
	}
		
	@CommitBusinessGlobalTransactional
	public GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> handleCommit(InvoiceCommitGlobalTransactionEvent event) {
		return GlobalTransactionStepDefinition.builder().
		in(event).
		meta(new WorkerMeta("InvoiceGlobalTransactionService")).build();
	}
	
	
	@RollbackBusinessGlobalTransactional
	public GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> handleRollBack(InvoiceRollbackGlobalTransactionEvent event) {
		return GlobalTransactionStepDefinition.builder().
		in(event).
		meta(new WorkerMeta("InvoiceGlobalTransactionService")).build();
	}
}
package com.example.demor2dbc.kermoss.saga.decoders;

import com.example.demor2dbc.kermoss.entities.CommandMeta;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;
import com.example.demor2dbc.kermoss.infra.BaseDecoder;
import com.example.demor2dbc.kermoss.saga.remote.InvoiceGlobalTransactionEvent;

public class PayInvoiceServiceDecoder implements BaseDecoder {

	@Override
	public BaseTransactionEvent decode(CommandMeta meta) {
		return new InvoiceGlobalTransactionEvent();
	}

}
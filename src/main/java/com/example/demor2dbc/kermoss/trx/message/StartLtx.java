package com.example.demor2dbc.kermoss.trx.message;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;

public class StartLtx {
    
	private final LocalTransactionStepDefinition pipeline;
	
	public StartLtx(LocalTransactionStepDefinition pipeline) {
	    this.pipeline=pipeline;	
	}

	public LocalTransactionStepDefinition getPipeline() {
		return pipeline;
	}
		
}
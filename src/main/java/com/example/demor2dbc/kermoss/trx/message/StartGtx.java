package com.example.demor2dbc.kermoss.trx.message;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;

public class StartGtx {
    
	private final GlobalTransactionStepDefinition pipeline;
	
	public StartGtx(GlobalTransactionStepDefinition pipeline) {
	    this.pipeline=pipeline;	
	}

	public GlobalTransactionStepDefinition getPipeline() {
		return pipeline;
	}
		
}
package com.example.demor2dbc.kermoss.trx.message;

import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;

public class CommitLtx {
    
	private final LocalTransactionStepDefinition pipeline;
	
	public CommitLtx(LocalTransactionStepDefinition pipeline) {
	    this.pipeline=pipeline;	
	}

	public LocalTransactionStepDefinition getPipeline() {
		return pipeline;
	}
		
}
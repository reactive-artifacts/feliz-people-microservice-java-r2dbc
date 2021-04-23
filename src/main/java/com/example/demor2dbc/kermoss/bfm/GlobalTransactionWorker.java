package com.example.demor2dbc.kermoss.bfm;
public abstract class GlobalTransactionWorker{
    protected final WorkerMeta meta;
    public GlobalTransactionWorker(
            final WorkerMeta meta
    ) {
        this.meta = meta;
    }    
    public WorkerMeta getMeta() {
		return meta;
	}
}
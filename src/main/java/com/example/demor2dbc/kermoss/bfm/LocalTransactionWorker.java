package com.example.demor2dbc.kermoss.bfm;
public abstract class LocalTransactionWorker{
    protected final WorkerMeta meta;
    public LocalTransactionWorker(
            final WorkerMeta meta
    ) {
        this.meta = meta;
    }    
    public WorkerMeta getMeta() {
		return meta;
	}
}
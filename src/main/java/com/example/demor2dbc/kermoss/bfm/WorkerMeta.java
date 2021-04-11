package com.example.demor2dbc.kermoss.bfm;

public class WorkerMeta {
  
    private final String transactionName;
    private String childOf;

    public WorkerMeta(String transactionName) {
        this.transactionName = transactionName;
    }

    public WorkerMeta(String transactionName, String childOf) {
        this.transactionName = transactionName;
        this.childOf = childOf;
    }

    public String getTransactionName() {
        return this.transactionName;
    }

    public String getChildOf() {
        return this.childOf;
    }

    public void setChildOf(String childOf) {
        this.childOf = childOf;
    }
}
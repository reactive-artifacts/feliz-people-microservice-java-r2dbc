package com.example.demor2dbc.kermoss.bfm;

import javax.validation.constraints.NotNull;

import com.example.demor2dbc.kermoss.domain.Message;

public abstract class BaseTransactionCommand<P> extends Message {
    private String subject;
    private String header;
    private P payload;
    private String destination;

    public BaseTransactionCommand(String subject, String header, P payload, String destination) {
        this.subject = subject;
        this.header = header;
        this.payload = payload;
        this.destination = destination;
    }

    public BaseTransactionCommand() {
    }

    public String getSubject() {
        return this.subject;
    }

    public String getHeader() {
        return this.header;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDestination(@NotNull String destination) {
        this.destination = destination;
    }
}

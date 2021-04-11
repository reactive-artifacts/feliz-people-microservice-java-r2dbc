package com.example.demor2dbc.kermoss.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;


public abstract class Message {

    @Id
    @JsonProperty("commandId")
    private String id = UUID.randomUUID().toString();
    @JsonProperty("startedTimestamp")
    private final Long timestamp = new Date().getTime();


    public Message() {
    }

    public String getId() {
        return id;
    }
}
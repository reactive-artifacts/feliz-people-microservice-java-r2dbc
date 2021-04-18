package com.example.demor2dbc.kermoss.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;


public abstract class Message {

    @Id
    //TODOx why commandId
    @JsonProperty("commandId")
    private String id = UUID.randomUUID().toString();
    @JsonProperty("startedTimestamp")
    private Long timestamp = new Date().getTime();


    public Message() {
    }

    public String getId() {
        return id;
    }

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}  
}
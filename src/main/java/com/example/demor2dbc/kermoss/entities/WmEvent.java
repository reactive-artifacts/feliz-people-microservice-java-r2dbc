package com.example.demor2dbc.kermoss.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("kermoss_event")
public class WmEvent {
    
	@Id
	private String id ;
	
	private final Long timestamp = new Date().getTime();
	
	private String payload;
	
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public Long getTimestamp() {
		return timestamp;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
}
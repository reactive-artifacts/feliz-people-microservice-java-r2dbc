package com.example.demor2dbc.kermoss.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("kermoss_event")
public class WmEvent {
    
	@Id
	private String id ;
	
	private Long timestamp = new Date().getTime();
	
	private String payload;
	
	private String name;
	
	private String GTX;
    private String LTX;
    private String FLTX;
    private String PGTX;
    private String commandId;
    private Status status = Status.PUBLISHED;

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
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public String getGTX() {
		return GTX;
	}

	public void setGTX(String gTX) {
		GTX = gTX;
	}

	public String getLTX() {
		return LTX;
	}

	public void setLTX(String lTX) {
		LTX = lTX;
	}

	public String getFLTX() {
		return FLTX;
	}

	public void setFLTX(String fLTX) {
		FLTX = fLTX;
	}

	public String getPGTX() {
		return PGTX;
	}

	public void setPGTX(String pGTX) {
		PGTX = pGTX;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}


	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}


	public enum Status {
        PUBLISHED,
        CONSUMED
    }
}
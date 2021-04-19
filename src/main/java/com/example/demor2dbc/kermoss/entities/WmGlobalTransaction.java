package com.example.demor2dbc.kermoss.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;


@Table("kermoss_gtx")
public class WmGlobalTransaction {
	@Id
	private String id;
	@Transient
	private boolean isNew = false;
	
	protected Long timestamp = new Date().getTime();
	private String name;

	private String parent;

	private GlobalTransactionStatus status = GlobalTransactionStatus.STARTED;

	
	public WmGlobalTransaction() {
	}
	
	public String getId() {
		return this.id;
	}
     
	
	public Long getTimestamp() {
		return this.timestamp;
	}

	
	public GlobalTransactionStatus getStatus() {
		return this.status;
	}

	
	public void setId(String id) {
		this.id = id;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}


	
	public void setStatus(GlobalTransactionStatus status) {
		this.status = status;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}



	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
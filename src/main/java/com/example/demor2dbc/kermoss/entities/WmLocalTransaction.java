package com.example.demor2dbc.kermoss.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("KERMOSS_LTX")
public class WmLocalTransaction {
	@Id
	private String id;
	@Transient
	private boolean isNew = false;

	protected Long timestamp = new Date().getTime();
	private String name;

	private LocalTransactionStatus state = LocalTransactionStatus.STARTED;

	private String fltx;
	@Column("ltx_id")
	private String ltxId;
	@Column("gtx_id")
	private String gtxId;

	public WmLocalTransaction() {
	}

	public String getId() {
		return this.id;
	}

	public Long getTimestamp() {
		return this.timestamp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
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

	public LocalTransactionStatus getState() {
		return state;
	}

	public void setState(LocalTransactionStatus state) {
		this.state = state;
	}

	public String getFltx() {
		return fltx;
	}

	public void setFltx(String fltx) {
		this.fltx = fltx;
	}

	public String getLtxId() {
		return ltxId;
	}

	public void setLtxId(String ltxId) {
		this.ltxId = ltxId;
	}

	public String getGtxId() {
		return gtxId;
	}

	public void setGtxId(String gtxId) {
		this.gtxId = gtxId;
	}

}
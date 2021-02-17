package com.example.demor2dbc.entities.write;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;

public abstract class WmEntity {
	@Id
	private Long id;
	private Boolean deleted;
	
	public Update toPq() {
		Map<SqlIdentifier, Object> columnsToUpdate = new LinkedHashMap<SqlIdentifier, Object>();
		setPq(columnsToUpdate);
		columnsToUpdate.put(SqlIdentifier.unquoted("deleted"), getDeleted());
		return Update.from(columnsToUpdate);

	}
	public Update toUq() {
		Map<SqlIdentifier, Object> columnsToUpdate = new LinkedHashMap<SqlIdentifier, Object>();
		setUq(columnsToUpdate);
		columnsToUpdate.put(SqlIdentifier.unquoted("deleted"), getDeleted());
		return Update.from(columnsToUpdate);
	}
	
	abstract void setUq(Map<SqlIdentifier, Object> columnsToUpdate);

	abstract void setPq(Map<SqlIdentifier, Object> columnsToUpdate);

	public WmEntity activate() {
		setDeleted(false);
		return this;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

}
package com.example.demor2dbc.entities.write;

import java.util.Map;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;

@Table("todo")
public class WmTodo extends WmEntity {

	public WmTodo() {
	}

	private String description;

	private String details;

	private Boolean done;

	@Column("person_id")
	private Long personId;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Boolean isDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	@Override
	void setPq(Map<SqlIdentifier, Object> columnsToUpdate) {

		if (getDescription() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("description"), getDescription());
		}
		if (getDetails() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("details"), getDetails());
		}

		if (isDone() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("done"), isDone());
		}

		if (columnsToUpdate.isEmpty()) {
			throw new RuntimeException("there is no column specified to update");
		}
	}

	@Override
	 void setUq(Map<SqlIdentifier, Object> columnsToUpdate) {
		columnsToUpdate.put(SqlIdentifier.unquoted("description"), getDescription());
		columnsToUpdate.put(SqlIdentifier.unquoted("details"), getDetails());
		columnsToUpdate.put(SqlIdentifier.unquoted("done"), isDone());
	}

}
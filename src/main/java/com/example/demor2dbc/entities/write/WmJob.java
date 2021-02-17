package com.example.demor2dbc.entities.write;

import java.util.Map;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import com.example.demor2dbc.statics.JobName;

@Table("job")
public class WmJob extends WmEntity {

	private JobName name;

	private String location;

	@Column(value = "developer_id")
	private Long personId;

	public JobName getName() {
		return name;
	}

	public void setName(JobName name) {
		this.name = name;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	 void setPq(Map<SqlIdentifier, Object> columnsToUpdate) {

		if (getName() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("name"), getName());
		}
		if (getLocation() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("location"), getLocation());
		}

	}

	@Override
	 void setUq(Map<SqlIdentifier, Object> columnsToUpdate) {
		columnsToUpdate.put(SqlIdentifier.unquoted("name"), getName());
		columnsToUpdate.put(SqlIdentifier.unquoted("location"), getLocation());

	}

}

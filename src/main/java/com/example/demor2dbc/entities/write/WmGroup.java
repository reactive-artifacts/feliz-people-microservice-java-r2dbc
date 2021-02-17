package com.example.demor2dbc.entities.write;

import java.util.Map;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import com.example.demor2dbc.statics.GroupName;

@Table("groupe")
public class WmGroup extends WmEntity {
	private GroupName name;

	@Column(value = "job_id")
	private Long jobId;

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public GroupName getName() {
		return name;
	}

	public void setName(GroupName name) {
		this.name = name;
	}

	@Override
	public void setPq(Map<SqlIdentifier, Object> columnsToUpdate) {

		if (getName() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("name"), getName());
		}

	}

	@Override
	public void setUq(Map<SqlIdentifier, Object> columnsToUpdate) {

		columnsToUpdate.put(SqlIdentifier.unquoted("name"), getName());

	}
}

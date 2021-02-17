package com.example.demor2dbc.entities.read;

import org.springframework.data.annotation.Id;

import com.example.demor2dbc.statics.GroupName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class Group {
	@Id
	private Long id;
	private GroupName name;
	private Job job;
	
	public Group() {
		super();
	}

	public Group(Long id, GroupName name, Job job) {
		super();
		this.id = id;
		this.name = name;
		this.job = job;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GroupName getName() {
		return name;
	}

	public void setName(GroupName name) {
		this.name = name;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
	
}

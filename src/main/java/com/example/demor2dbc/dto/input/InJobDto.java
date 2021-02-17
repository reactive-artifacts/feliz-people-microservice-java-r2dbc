package com.example.demor2dbc.dto.input;

import java.util.List;

import com.example.demor2dbc.statics.JobName;
public class InJobDto {
	private Long id;
	private JobName name;
	private String location;
	private List<InGroupDto> groups;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JobName getName() {
		return name;
	}

	public void setName(JobName name) {
		this.name = name;
	}

		
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<InGroupDto> getGroups() {
		return groups;
	}

	public void setGroups(List<InGroupDto> groups) {
		this.groups = groups;
	}
}

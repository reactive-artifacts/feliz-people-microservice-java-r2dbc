package com.example.demor2dbc.web.dto.response;

import java.util.List;

import com.example.demor2dbc.statics.JobName;
public class HrJobDto  extends HrEntityDto{
	
	
	private JobName name;
	private String location;
	private List<HrGroupDto> groups;
	

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

	public List<HrGroupDto> getGroups() {
		return groups;
	}

	public void setGroups(List<HrGroupDto> groups) {
		this.groups = groups;
	}
}

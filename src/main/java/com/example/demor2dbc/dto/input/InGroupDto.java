package com.example.demor2dbc.dto.input;

import com.example.demor2dbc.statics.GroupName;

public class InGroupDto {
	private Long id;
	private GroupName name;
		
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

	
}

package com.example.demor2dbc.entities.read;

import org.springframework.data.annotation.Id;

public class Tag {
	@Id
	private Long id;
	private String name;
	
	
	
	public Tag(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
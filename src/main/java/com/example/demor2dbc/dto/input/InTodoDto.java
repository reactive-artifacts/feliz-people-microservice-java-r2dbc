package com.example.demor2dbc.dto.input;

import java.util.Set;

public class InTodoDto {

	private Long id;

	private String description;

	private String details;

	private boolean done;

	private Set<InTagDto> tags;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Set<InTagDto> getTags() {
		return tags;
	}

	public void setTags(Set<InTagDto> tags) {
		this.tags = tags;
	}

	
}
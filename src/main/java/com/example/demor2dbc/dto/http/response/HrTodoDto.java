package com.example.demor2dbc.dto.http.response;

import java.util.Set;

public class HrTodoDto extends HrEntityDto {


	private String description;

	private String details;

	private boolean done;

	private Set<HrTagDto> tags;

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

	public Set<HrTagDto> getTags() {
		return tags;
	}

	public void setTags(Set<HrTagDto> tags) {
		this.tags = tags;
	}

	
}
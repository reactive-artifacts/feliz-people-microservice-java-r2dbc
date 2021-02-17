package com.example.demor2dbc.dto.input;

import java.util.Set;

public class InPersonDto {
	private Long id;
	private String name;
	private String address;
	private Set<InJobDto> jobs;

	private Set<InTodoDto> todos;
  
	
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Set<InJobDto> getJobs() {
		return jobs;
	}

	public void setJobs(Set<InJobDto> jobs) {
		this.jobs = jobs;
	}

	public Set<InTodoDto> getTodos() {
		return todos;
	}

	public void setTodos(Set<InTodoDto> todos) {
		this.todos = todos;
	}

}
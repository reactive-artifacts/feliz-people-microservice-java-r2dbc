package com.example.demor2dbc.dto.input;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class InPersonDto {
	private Long id;
	private String name;
	private String address;

	private String descript;

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	private Set<InJobDto> jobs;
	@JsonIgnore
	private String userId;

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

	public String getUserId() {
		return userId;
	}

	@JsonIgnore
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
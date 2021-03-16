package com.example.demor2dbc.web.dto.response;

import java.util.Set;

public class HrPersonDto extends HrEntityDto {
	private String name;
	private String address;

	private Set<HrJobDto> jobs;

	private Set<HrTodoDto> todos;

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

	public Set<HrJobDto> getJobs() {
		return jobs;
	}

	public void setJobs(Set<HrJobDto> jobs) {
		this.jobs = jobs;
	}

	public Set<HrTodoDto> getTodos() {
		return todos;
	}

	public void setTodos(Set<HrTodoDto> todos) {
		this.todos = todos;
	}

}
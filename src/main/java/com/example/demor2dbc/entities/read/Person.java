package com.example.demor2dbc.entities.read;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Person {
	@Id
	private Long id;
	private String name;
	private String address;
	private String userId;
	private Set<Job> jobs;

	private Set<Todo> todos;

	public Person() {
		super();
	}

	public Person(Long id) {
		super();
		this.id = id;
	}

	public Person(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Set<Job> getJobs() {
		if (jobs == null) {
			return new HashSet<>();
		}

		return jobs;
	}

	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}

	public Set<Todo> getTodos() {
		if (todos == null) {
			return new HashSet<>();
		}

		return todos;
	}

	public void setTodos(Set<Todo> todos) {
		this.todos = todos;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

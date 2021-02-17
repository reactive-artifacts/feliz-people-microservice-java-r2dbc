package com.example.demor2dbc.entities.read;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import com.example.demor2dbc.statics.JobName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class Job {
	@Id
	private Long id;
	private JobName name;
	private String location;
	//work only on jdbc , and only for one to one relationship and "developer_id" must exist on table person , 
	//many to one not supported
	//@Column(value = "developer_id")
	private Long developerId;
	//work only on spring data jdbc
	@MappedCollection(idColumn = "job_id",keyColumn = "id")
	private List<Group> groups;
	
    
    public Job() {
		super();
	}
    

	public Job(Long id, JobName name, Long developerId) {
		super();
		this.id = id;
		this.name = name;
		this.developerId = developerId;
	}


	public Job(Long id) {
		super();
		this.id = id;
	}


	

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

		
	public List<Group> getGroups() {
		if (groups == null) {
			return new ArrayList<>();
		}
		
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}


	public Long getDeveloperId() {
		return developerId;
	}


	public void setDeveloperId(Long developerId) {
		this.developerId = developerId;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}
}

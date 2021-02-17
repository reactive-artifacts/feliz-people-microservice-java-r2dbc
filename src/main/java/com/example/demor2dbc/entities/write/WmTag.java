package com.example.demor2dbc.entities.write;

import java.util.Map;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;



@Table("tag")
public class WmTag  extends WmEntity{
	private String name;
	
	
	public WmTag() {
		super();
	}

	public WmTag(String name) {
		super();
		this.name = name;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	 void setUq(Map<SqlIdentifier, Object> columnsToUpdate) {
	
	}
	
	@Override
	void setPq(Map<SqlIdentifier, Object> columnsToUpdate) {
	}
	
	
	
}
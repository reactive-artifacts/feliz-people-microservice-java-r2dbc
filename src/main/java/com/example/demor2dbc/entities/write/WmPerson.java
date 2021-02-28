package com.example.demor2dbc.entities.write;

import java.util.Map;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Table("person")
public class WmPerson extends WmEntity {

	private String name;
	private String address;
	private String userId;

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
	
	@Override
	 void setPq(Map<SqlIdentifier, Object> columnsToUpdate) {
		if (getName() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("name"), getName());
		}
		if (getAddress() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("address"), getAddress());
		}


	}
	@Override
	 void setUq(Map<SqlIdentifier, Object> columnsToUpdate) {
		columnsToUpdate.put(SqlIdentifier.unquoted("name"), getName());
		columnsToUpdate.put(SqlIdentifier.unquoted("address"), getAddress());

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "WmPerson [name=" + name + ", address=" + address + ", userId=" + userId + "]";
	}
	
	

}

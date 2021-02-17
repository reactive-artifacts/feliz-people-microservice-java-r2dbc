package com.example.demor2dbc.entities.write;

import java.util.Map;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;

@Table("todo_tag")
public class WmTodoTag extends WmEntity {

	public WmTodoTag() {
	}

	@Column("todo_id")
	private Long todoId;

	@Column("tag_id")
	private Long tagId;

	public WmTodoTag(Long todoId, Long tagId) {
		super();
		this.todoId = todoId;
		this.tagId = tagId;
	}

	@Override
	void setUq(Map<SqlIdentifier, Object> columnsToUpdate) {

	}
	
	
	@Override
	 void setPq(Map<SqlIdentifier, Object> columnsToUpdate) {

	}

}
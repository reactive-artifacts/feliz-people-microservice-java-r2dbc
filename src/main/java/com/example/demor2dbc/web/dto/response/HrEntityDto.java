package com.example.demor2dbc.web.dto.response;

public class HrEntityDto {
    private Long id;

	public HrEntityDto() {
		super();
	}
	

	public HrEntityDto(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
}

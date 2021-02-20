package com.example.demor2dbc.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class UserDto {
	private String sub;
	private String userName;
	private String email;
	private Collection<GrantedAuthority> authority;

	

	public UserDto(String sub,String userName, String email, Collection<GrantedAuthority> authority) {
		super();
		this.sub=sub;
		this.userName = userName;
		this.email = email;
		this.authority = authority;
	}

	public String getSub() {
		return sub;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public Collection<GrantedAuthority> getAuthority() {
		return authority;
	}

}
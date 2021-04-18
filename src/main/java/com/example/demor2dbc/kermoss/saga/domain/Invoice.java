package com.example.demor2dbc.kermoss.saga.domain;

import java.math.BigDecimal;

public class Invoice {
	private BigDecimal price;
	private String address;
	
	public Invoice(BigDecimal price, String address) {
		super();
		this.price = price;
		this.address = address;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}

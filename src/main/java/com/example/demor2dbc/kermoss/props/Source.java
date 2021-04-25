package com.example.demor2dbc.kermoss.props;

public class Source {
	
	private String http;
	private String feign;
	private String kafka;
	private Layer transport;

	public String getHttp() {
		return http;
	}

	public void setHttp(String http) {
		this.http = http;
	}

	public String getFeign() {
		return feign;
	}

	public void setFeign(String feign) {
		this.feign = feign;
	}

	public String getKafka() {
		return kafka;
	}

	public void setKafka(String kafka) {
		this.kafka = kafka;
	}

	public Layer getTransport() {
		return transport;
	}

	public void setTransport(Layer transport) {
		this.transport = transport;
	} 
	
}
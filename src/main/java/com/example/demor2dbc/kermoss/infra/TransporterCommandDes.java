package com.example.demor2dbc.kermoss.infra;

import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;

import com.example.demor2dbc.infra.kafka.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransporterCommandDes implements Deserializer<TransporterCommand> {
	private final ObjectMapper objectMapper = JacksonUtils.create();
	@Override
	public TransporterCommand deserialize(String topic, byte[] data) {
		try {
			return objectMapper.readValue(data, TransporterCommand.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
    
}

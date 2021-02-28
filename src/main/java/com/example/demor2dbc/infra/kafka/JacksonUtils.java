package com.example.demor2dbc.infra.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JacksonUtils {
    public static ObjectMapper create(){
    	ObjectMapper objectMapper = JsonMapper.builder()
    			.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
    			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    			.build();
    	objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    	//.registerModule(new ParameterNamesModule())
    	objectMapper.registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule());
       return objectMapper;
    }
}

package com.example.demor2dbc;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demor2dbc.events.DomainEvent;
import com.example.demor2dbc.infra.kafka.JsonSerializer;

import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaSenderConfig {

	private static final String BOOTSTRAP_SERVERS = "localhost:9092";
       
    
    @Bean
    public SenderOptions<String, DomainEvent> senderOptions(){
    	Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "person-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return  SenderOptions.<String,DomainEvent>create(props);
        		
    }
       
   }
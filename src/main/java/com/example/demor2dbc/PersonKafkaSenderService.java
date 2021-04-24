package com.example.demor2dbc;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.events.DomainEvent;

import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class PersonKafkaSenderService {
	public static final Logger LOG = Loggers.getLogger(PersonKafkaSenderService.class);
	
    private  KafkaSender<String, DomainEvent> sender;
	
    @Autowired
    private SenderOptions<String, DomainEvent> senderOptions;
    
    @Autowired
    private PersonEventFluxBridge personBridge;
    
   
    @PostConstruct
    public void  init() {
      	
      sender = KafkaSender.create(senderOptions);
      sender.send(events()).subscribe();

    }
    
    private Flux<SenderRecord<String, DomainEvent, UUID>> events(){
    	return Flux.merge(List.of(personBridge.flux())).
    			onBackpressureBuffer(255*3, x->{
    				//making some side effect here 
    				LOG.warn("Overflow: discarding element from queue="+x);
    				}, 
    					BufferOverflowStrategy.DROP_LATEST)
    			.onErrorContinue((ex,object)->{
    		LOG.error("connot send event to kafka"+object.toString(),ex);
    	}).map(evt->toSenderRecord(evt));
    }
    
     
    private SenderRecord<String, DomainEvent, UUID> toSenderRecord(DomainEvent evt){
    	
    	ProducerRecord<String, DomainEvent> producerRecord = new ProducerRecord<String, DomainEvent>
			(evt.topic(), evt.keyAsUUID().toString(),evt);
		
		return SenderRecord.create(producerRecord,UUID.randomUUID());
    }
    
    @PreDestroy
    public void destroy() {
       sender.close();
    }
}
package com.example.demor2dbc.kermoss.infra;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.entities.WmInboundCommand;
import com.example.demor2dbc.kermoss.service.BusinessFlow;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

@Component
public class KafkaReceiverService {
	
	@Autowired
	private BusinessFlow businessFlow; 

	public ReceiverOptions<String, TransporterCommand> receiverOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "xPerson");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TransporterCommandDes.class);
        return ReceiverOptions.<String, TransporterCommand>create(props);
    }
	
	@PostConstruct
	public void init() {
		
		 ReceiverOptions<String, TransporterCommand> options = 
				 receiverOptions().subscription(Collections.singleton("xxPerson11")).
				 commitInterval(Duration.ZERO);
		 
		 Flux<ReceiverRecord<String, TransporterCommand>> kafkaFlux = KafkaReceiver.create(options).receive();
		 
		 kafkaFlux
		 .concatMap(m->businessFlow.recieveInBoundCommand(transform(m.value()))
				 .thenEmpty(m.receiverOffset().commit())
				 )
		 // test then vs thenEmpty
		 //retry() understand retry from line 175 on https://github.com/reactor/reactor-kafka/blob/main/reactor-kafka-samples/src/main/java/reactor/kafka/samples/SampleScenarios.java 
		 .subscribe();
		
	}
	
	public WmInboundCommand transform(TransporterCommand outcmd) {

		return WmInboundCommand.builder().source(outcmd.getSource()).subject(outcmd.getSubject())
				.destination(outcmd.getDestination()).payload(outcmd.getPayload()).PGTX(outcmd.getParentGTX())
				.gTX(outcmd.getChildofGTX()).fLTX(outcmd.getFLTX()).additionalHeaders(outcmd.getAdditionalHeaders())
				.refId(outcmd.getRefId()).trace(outcmd.getTraceId()).build();
	}
}

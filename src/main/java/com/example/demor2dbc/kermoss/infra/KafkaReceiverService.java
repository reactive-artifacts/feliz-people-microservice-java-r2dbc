package com.example.demor2dbc.kermoss.infra;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.PersonService;
import com.example.demor2dbc.kermoss.entities.WmInboundCommand;
import com.example.demor2dbc.kermoss.props.KermossProperties;
import com.example.demor2dbc.kermoss.service.BusinessFlow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class KafkaReceiverService {

	public static final Logger LOG = Loggers.getLogger(KafkaReceiverService.class);

	@Autowired
	private BusinessFlow businessFlow;

	@Autowired
	private KermossProperties kermossProperties;

	@Autowired
	private ObjectMapper objectMapper;

	public ReceiverOptions<String, String> receiverOptions() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "xPerson");
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return ReceiverOptions.<String, String>create(props);
	}

	
	
	private Disposable subscribe;
	@PostConstruct
	public void init() {

		ReceiverOptions<String, String> options = receiverOptions().subscription(kermossProperties.topics())
				.commitInterval(Duration.ZERO).
				commitBatchSize(0);
		Flux<ReceiverRecord<String, String>> kafkaFlux = KafkaReceiver.create(options).receive();

		subscribe = kafkaFlux.concatMap(m -> {
			TransporterCommand tc = null;
			try {
				tc = objectMapper.readValue(m.value(), TransporterCommand.class);
			} catch (JsonProcessingException e) {
				LOG.error("Cannot deserialize the " + m.value() + " to TransporterCommand", e);
			}
			
			if (tc != null) {
				return businessFlow.recieveInBoundCommand(transform(tc)).thenEmpty(m.receiverOffset().commit());
			} else {
				return m.receiverOffset().commit();
			}
		}

		).retry() // retry if any error occurred like database connectivity.
		.subscribe();

	}
	
	@PreDestroy
	public void destroy() {
		subscribe.dispose();
	}

	public WmInboundCommand transform(TransporterCommand outcmd) {

		return WmInboundCommand.builder().source(outcmd.getSource()).subject(outcmd.getSubject())
				.destination(outcmd.getDestination()).payload(outcmd.getPayload()).PGTX(outcmd.getParentGTX())
				.gTX(outcmd.getChildofGTX()).fLTX(outcmd.getFLTX()).additionalHeaders(outcmd.getAdditionalHeaders())
				.refId(outcmd.getRefId()).trace(outcmd.getTraceId()).build();
	}
	
}

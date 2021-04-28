package com.example.demor2dbc.kermoss.infra;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.example.demor2dbc.infra.kafka.JsonSerializer;
import com.example.demor2dbc.kermoss.entities.WmOutboundCommand;
import com.example.demor2dbc.kermoss.props.KermossProperties;
import com.example.demor2dbc.kermoss.props.Layer;

import reactor.core.Disposable;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class KafkaSenderService {
	public static final Logger LOG = Loggers.getLogger(KafkaSenderService.class);

	@Autowired
	private ReactiveTransactionManager tm;

	@Autowired
	private OutBoundCommandFlux outboundCommandFlux;

	@Autowired
	private R2dbcEntityTemplate template;

	@Autowired
	private KermossProperties kermossProperties;

	private KafkaSender<String, TransporterCommand> sender;
	
	private Disposable subscribe;

	// TODOx pass to config class
	public SenderOptions<String, TransporterCommand> senderOptions() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "service_name"+"-producer");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return SenderOptions.<String, TransporterCommand>create(props);

	}

	
	@PostConstruct
	public void init() {
		TransactionalOperator rxtx = TransactionalOperator.create(tm);
		sender = KafkaSender.create(senderOptions());
		subscribe=sender.send(events()).flatMap(rs -> {
			WmOutboundCommand wmOutboundCommand = new WmOutboundCommand();
			if (rs.exception() == null) {
				wmOutboundCommand.changeStatusToDelivered();
			} else {
				wmOutboundCommand.changeStatusToFailed();
			}
			return template.update(Query.query(Criteria.where("id").is(rs.correlationMetadata())),
					toPq(wmOutboundCommand), WmOutboundCommand.class).as(rxtx::transactional);
		}).onErrorContinue((ex, object) -> {
			LOG.error("connot send event to kafka" + object.toString(), ex);
		}).subscribe();

	}
	
	

	private Flux<SenderRecord<String, TransporterCommand, String>> events() {
		return Flux.merge(List.of(outboundCommandFlux.flux().filter(tc -> Layer.KAFKA.equals(getTransportLayer(tc)))))
				.onBackpressureBuffer(255 * 3, x -> {
					// making some side effect here
					LOG.warn("Overflow: discarding element from queue=" + x);
				}, BufferOverflowStrategy.DROP_LATEST).map(tc -> toSenderRecord(tc));
	}

	private SenderRecord<String, TransporterCommand, String> toSenderRecord(TransporterCommand command) {

		ProducerRecord<String, TransporterCommand> producerRecord = new ProducerRecord<String, TransporterCommand>(
				getAddress(command), command.getRefId(), command);
		return SenderRecord.create(producerRecord, command.getRefId());
	}

	@PreDestroy
	public void destroy() {
		sender.close();
		subscribe.dispose();
	}

	public Update toPq(WmOutboundCommand wmo) {
		Map<SqlIdentifier, Object> columnsToUpdate = new LinkedHashMap<SqlIdentifier, Object>();
		columnsToUpdate.put(SqlIdentifier.unquoted("status"), wmo.getStatus());
		if (wmo.getDeliveredTimestamp() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("delivered_timestamp"), wmo.getDeliveredTimestamp());
		}
		if (wmo.getFailedTimestamp() != null) {
			columnsToUpdate.put(SqlIdentifier.unquoted("failed_timestamp"), wmo.getFailedTimestamp());
		}
		return Update.from(columnsToUpdate);

	}

	Layer getTransportLayer(TransporterCommand tc) {
		Layer layer = kermossProperties.getSources().get(tc.getDestination()).getTransport();
		if (layer == null) {
			layer = kermossProperties.getTransport().getDefaultLayer();
		}
		return layer;
	}

	String getAddress(TransporterCommand tc) {
		String address = null;
		Layer layer = getTransportLayer(tc);
		switch (layer) {
		case HTTP:
			address = kermossProperties.getHttpDestination(tc.getDestination());
			break;
		case KAFKA:
			address = kermossProperties.getKafkaDestination(tc.getDestination());
			break;
		}
		return address;
	}
}
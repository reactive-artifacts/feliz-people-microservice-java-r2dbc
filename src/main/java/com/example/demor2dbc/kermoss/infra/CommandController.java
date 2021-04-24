package com.example.demor2dbc.kermoss.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demor2dbc.kermoss.entities.WmInboundCommand;
import com.example.demor2dbc.kermoss.service.BusinessFlow;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/command-executor")
public class CommandController {
    
    @Autowired
	private BusinessFlow businessFlow; 
	
    @PostMapping("/commands")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createCommand(@RequestBody final TransporterCommand command) {
    	//TODOx check l'idempotence if exist...
    	return businessFlow.recieveInBoundCommand(transform(command));
    }
    
    
    //TODOx to be refactored see KafkaReceiver 
    public WmInboundCommand transform(TransporterCommand outcmd) {

		return WmInboundCommand.builder().source(outcmd.getSource()).subject(outcmd.getSubject())
				.destination(outcmd.getDestination()).payload(outcmd.getPayload()).PGTX(outcmd.getParentGTX())
				.gTX(outcmd.getChildofGTX()).fLTX(outcmd.getFLTX()).additionalHeaders(outcmd.getAdditionalHeaders())
				.refId(outcmd.getRefId()).trace(outcmd.getTraceId()).build();
	}

}
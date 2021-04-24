package com.example.demor2dbc.kermoss.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.entities.WmOutboundCommand;
import com.example.demor2dbc.kermoss.events.OutboundCommandStarted;

import reactor.core.publisher.Mono;

@Component
public class DefaultCommandTransporter {
	@Autowired
	OutBoundCommandFlux boundCommandFlux;
	
	@Autowired
	private R2dbcEntityTemplate template;
	
	@EventListener
	public Mono<Void> onEvent(final OutboundCommandStarted event) {
	                                             
	        return template.selectOne(Query.query(Criteria.where("id").is(event.getMeta().getCommandId())), 
	        		 WmOutboundCommand.class).doOnNext(wmc->{
	        			 TransporterCommand c = transform(wmc);
	        			 boundCommandFlux.bridge().accept(c);
	        		 }).then();
		
	}
	
	
	protected TransporterCommand transform(WmOutboundCommand outcmd){
        return new TransporterCommand(outcmd.getSubject(),
                outcmd.getSource(),
                outcmd.getDestination(),
                outcmd.getPGTX() != null ? outcmd.getPGTX() : null,
                outcmd.getLTX(),
                outcmd.getPGTX() == null ? outcmd.getGTX() : null,
                outcmd.getAdditionalHeaders(),
                outcmd.getPayload(),
                outcmd.getId(),
                outcmd.getTraceId()
                );
    }

}
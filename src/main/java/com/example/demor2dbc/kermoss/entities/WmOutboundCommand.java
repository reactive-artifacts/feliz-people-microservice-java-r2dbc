package com.example.demor2dbc.kermoss.entities;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WmOutboundCommand extends WmAbstractCommand {
    private static final Logger log = LoggerFactory.getLogger(WmOutboundCommand.class);

    private static final String POOL_NAME="OUT_CMD";
	private Status status = Status.STARTED;
    private Long deliveredTimestamp;

    
    public WmOutboundCommand() {
    	pool=POOL_NAME;
    }

    public WmOutboundCommand(String subject, String source, String destination, String gTX, String lTX, String fLTX,String PGTX,
                           String additionalHeaders, Status status, String payload, String traceId) {
        super(subject, source, destination, gTX, lTX, fLTX,PGTX,additionalHeaders,payload, traceId);
        pool=POOL_NAME;
        this.status=status;
    }

    public static WmOutboundCommandBuilder builder() {
        return new WmOutboundCommandBuilder();
    }

    public enum Status {
        PREPARED,
        STARTED,
        DELIVERED,
        FAILED
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeStatus(final Status status) {
        final Long timestamp = new Date().getTime();
        if(status.equals(Status.DELIVERED)) {
            this.setDeliveredTimestamp(timestamp);
        } else if(status.equals(Status.FAILED)) {
            this.setFailedTimestamp(timestamp);
        }
        this.setStatus(status);
    }

    public void changeStatusToDelivered() {
        this.changeStatus(Status.DELIVERED);
    }

    public void changeStatusToFailed() {
        this.changeStatus(Status.FAILED);
    }

    public Long getDeliveredTimestamp() {
        return deliveredTimestamp;
    }

    public void setDeliveredTimestamp(Long deliveredTimestamp) {
        this.deliveredTimestamp = deliveredTimestamp;
    }

    public static class WmOutboundCommandBuilder {
        private String subject;
        private String source;
        private String destination;
        private String gTX;
        private String lTX;
        private String fLTX;
        private String PGTX;
        private String additionalHeaders;
        private Status status = Status.STARTED;
        private String payload;
        private String traceId;

         WmOutboundCommandBuilder() {
        }

        public WmOutboundCommandBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public WmOutboundCommandBuilder source(String source) {
            this.source = source;
            return this;
        }

        public WmOutboundCommandBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public WmOutboundCommandBuilder gTX(String gTX) {
            this.gTX = gTX;
            return this;
        }
        
        
        public WmOutboundCommandBuilder pGTX(String PGTX) {
            this.PGTX = PGTX;
            return this;
        }

        public WmOutboundCommandBuilder lTX(String lTX) {
            this.lTX = lTX;
            return this;
        }

        public WmOutboundCommandBuilder fLTX(String fLTX) {
            this.fLTX = fLTX;
            return this;
        }

        public WmOutboundCommandBuilder additionalHeaders(String additionalHeaders) {
            this.additionalHeaders = additionalHeaders;
            return this;
        }

        public WmOutboundCommandBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public WmOutboundCommandBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public WmOutboundCommandBuilder trace(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public WmOutboundCommand build() {
            return new WmOutboundCommand(subject, source, destination, gTX, lTX, fLTX,PGTX, additionalHeaders, status, payload, traceId);
        }

        public String toString() {
            return "OutboundCommand.WmOutboundCommandBuilder(subject=" + this.subject + ", source=" + this.source + ", destination=" + this.destination + ", gTX=" + this.gTX + ", lTX=" + this.lTX + ", fLTX=" + this.fLTX + ", additionalHeaders=" + this.additionalHeaders + ", status=" + this.status + ", payload=" + this.payload + ")";
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

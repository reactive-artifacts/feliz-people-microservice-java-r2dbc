package com.example.demor2dbc.kermoss.entities;


import java.util.Date;


public class WmInboundCommand extends WmAbstractCommand {

	private static final String POOL_NAME="IN_CMD";
	private Status status = Status.STARTED;
    private Long completedTimestamp;
    private String refId;

    public WmInboundCommand() {
    	pool=POOL_NAME;
    }

    public WmInboundCommand(String subject, String source, String destination, String GTX, String LTX, String FLTX, String PGTX, String additionalHeaders, String payload, Status status, String refId, String traceId) {
        super(subject, source, destination, GTX, LTX, FLTX, PGTX, additionalHeaders, payload, traceId);
        pool=POOL_NAME;
        this.status = status;
        this.refId = refId;
    }
    
    public WmInboundCommand( String subject, String source, String destination, String GTX, String LTX, String FLTX, String PGTX, String additionalHeaders, String payload, Status status, String traceId) {
        super(subject, source, destination, GTX, LTX, FLTX, PGTX, additionalHeaders, payload, traceId);
        this.status = status;
    }

    public static WmInboundCommandBuilder builder() {
        return new WmInboundCommandBuilder();
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeStatus(final Status status) {
        final Long timestamp = new Date().getTime();
        if(status.equals(Status.COMPLETED)) {
            this.setCompletedTimestamp(timestamp);
        } else if(status.equals(Status.FAILED)) {
            this.setFailedTimestamp(timestamp);
        }
        this.setStatus(status);
    }

    public void changeStatusToCompleted() {
        this.changeStatus(Status.COMPLETED);
    }

    public void changeStatusToFailed() {
        this.changeStatus(Status.FAILED);
    }

    public Long getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompletedTimestamp(Long completedTimestamp) {
        this.completedTimestamp = completedTimestamp;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public enum Status {
        PREPARED,
        STARTED,
        COMPLETED,
        FAILED
    }

    


    public static class WmInboundCommandBuilder {
        private String subject;
        private String source;
        private String destination;
        private String gTX;
        private String lTX;
        private String fLTX;
        private String PGTX;
        private String additionalHeaders;
        private Status status = Status.STARTED;
        private String traceId;

        private String payload;
        private String refId;

        WmInboundCommandBuilder() {
        }

        public WmInboundCommandBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public WmInboundCommandBuilder source(String source) {
            this.source = source;
            return this;
        }

        public WmInboundCommandBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public WmInboundCommandBuilder gTX(String gTX) {
            this.gTX = gTX;
            return this;
        }

        public WmInboundCommandBuilder lTX(String lTX) {
            this.lTX = lTX;
            return this;
        }

        public WmInboundCommandBuilder fLTX(String fLTX) {
            this.fLTX = fLTX;
            return this;
        }

        public WmInboundCommandBuilder PGTX(String PGTX) {
            this.PGTX = PGTX;
            return this;
        }

        public WmInboundCommandBuilder additionalHeaders(String additionalHeaders) {
            this.additionalHeaders = additionalHeaders;
            return this;
        }

        public WmInboundCommandBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public WmInboundCommandBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public WmInboundCommandBuilder refId(String refId) {
            this.refId= refId;
            return this;
        }

        public WmInboundCommandBuilder trace(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public WmInboundCommand build() {
            return new WmInboundCommand(subject, source, destination, gTX, lTX, fLTX, PGTX, additionalHeaders, payload, status, refId, traceId);
        }

        @Override
        public String toString() {
            return "WmInboundCommandBuilder{" +
                    "subject='" + subject + '\'' +
                    ", source='" + source + '\'' +
                    ", destination='" + destination + '\'' +
                    ", gTX='" + gTX + '\'' +
                    ", lTX='" + lTX + '\'' +
                    ", fLTX='" + fLTX + '\'' +
                    ", PGTX='" + PGTX + '\'' +
                    ", additionalHeaders='" + additionalHeaders + '\'' +
                    ", status=" + status +
                    ", payload='" + payload + '\'' +
                    ", refId='" + refId + '\'' +
                    '}';
        }
    }
}

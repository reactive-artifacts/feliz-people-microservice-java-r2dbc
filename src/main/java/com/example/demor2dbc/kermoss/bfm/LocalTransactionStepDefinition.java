package com.example.demor2dbc.kermoss.bfm;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.example.demor2dbc.kermoss.events.BaseLocalTransactionEvent;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;


public class LocalTransactionStepDefinition<T extends BaseLocalTransactionEvent>  {
    private WorkerMeta meta;
    private Stream<BaseTransactionEvent> blow;
    private Stream<BaseTransactionCommand> send;
    private T in;
    

	public LocalTransactionStepDefinition(T in, Stream<BaseTransactionEvent> blow,Stream<BaseTransactionCommand> send,WorkerMeta meta) {
        this.in=in;
		this.meta = meta;
		this.blow=blow;
		this.send=send;
    }

    public LocalTransactionStepDefinition() {
    }

    public static <T extends BaseLocalTransactionEvent> LocalTransactionPipelineBuilder<T> builder() {
        return new LocalTransactionPipelineBuilder<T>();
    }

    public WorkerMeta getMeta() {
        return this.meta;
    }
    
    public Stream<BaseTransactionEvent> getBlow() {
		return blow;
	}
    
    public T getIn() {
		return in;
	}
    
    public Stream<BaseTransactionCommand> getSend() {
		return send;
	}

	public static class LocalTransactionPipelineBuilder<T extends BaseLocalTransactionEvent> {
        
    	private T in;
        private Optional<Supplier> process;
        private Stream<BaseTransactionEvent> blow;
        private Stream<BaseTransactionCommand> send;
        private WorkerMeta meta;
        

        LocalTransactionPipelineBuilder() {
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> in(T in) {
            this.in = in;
            return this;
        }
        

        
        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> blow(Stream<BaseTransactionEvent> blow) {
            this.blow = blow;
            return this;
        }
        
        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> send(Stream<BaseTransactionCommand> send) {
            this.send = send;
            return this;
        }

        public LocalTransactionStepDefinition.LocalTransactionPipelineBuilder<T> meta(WorkerMeta meta) {
            this.meta = meta;
            return this;
        }

        
        

        public LocalTransactionStepDefinition<T> build() {
            return new LocalTransactionStepDefinition<T>(in,blow,send,meta);
        }

		
    }
}

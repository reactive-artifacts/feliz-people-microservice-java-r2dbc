package com.example.demor2dbc.kermoss.bfm;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;


public class GlobalTransactionStepDefinition<T extends BaseGlobalTransactionEvent>  {
    private WorkerMeta meta;
    private Stream<BaseTransactionEvent> blow;
    private T in;
    

	public GlobalTransactionStepDefinition(T in, Stream<BaseTransactionEvent> blow, WorkerMeta meta) {
        this.in=in;
		this.meta = meta;
		this.blow=blow;
    }

    public GlobalTransactionStepDefinition() {
    }

    public static <T extends BaseGlobalTransactionEvent> GlobalTransactionPipelineBuilder<T> builder() {
        return new GlobalTransactionPipelineBuilder<T>();
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
    
      public static class GlobalTransactionPipelineBuilder<T extends BaseGlobalTransactionEvent> {
        
    	private T in;
        private Optional<Supplier> process;
        private Stream<BaseTransactionEvent> blow;
        private WorkerMeta meta;
        

        GlobalTransactionPipelineBuilder() {
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> in(T in) {
            this.in = in;
            return this;
        }
        

        
        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> blow(Stream<BaseTransactionEvent> blow) {
            this.blow = blow;
            return this;
        }

        public GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> meta(WorkerMeta meta) {
            this.meta = meta;
            return this;
        }

        
        

        public GlobalTransactionStepDefinition<T> build() {
            return new GlobalTransactionStepDefinition<T>(in,blow, meta);
        }

		
    }
}

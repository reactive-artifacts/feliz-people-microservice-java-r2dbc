package com.example.demor2dbc.kermoss.bfm;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.example.demor2dbc.kermoss.events.BaseGlobalTransactionEvent;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class GlobalTransactionStepDefinition<T extends BaseGlobalTransactionEvent>  {
    private  WorkerMeta meta;
    private Stream<BaseTransactionEvent> blow;
    private T in;
    private ReceivedCommand receivedCommad;
    private CompensateWhen compensateWhen;

	public GlobalTransactionStepDefinition(T in, Stream<BaseTransactionEvent> blow, WorkerMeta meta,
			ReceivedCommand receivedCommad,CompensateWhen compensateWhen) {
        this.in=in;
		this.meta = meta;
		this.blow=blow;
		this.receivedCommad=receivedCommad;
		this.compensateWhen=compensateWhen;
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
    
    
    
    public <P> ReceivedCommand<P> getReceivedCommad() {
		return receivedCommad;
	}

    
    public CompensateWhen getCompensateWhen() {
		return compensateWhen;
	}


	public static class CompensateWhen<E extends Class<? extends Exception>> {
		private E[] exceptionClazz;
		private Flux<BaseTransactionEvent> blow;
		private Propagation propagation = Propagation.LOCAL;

		
		
		public CompensateWhen(Propagation propagation, Flux<BaseTransactionEvent> blow, E... exceptionClazz) {
			this.propagation = propagation;
			this.blow = blow;
			this.exceptionClazz = exceptionClazz;
		}
		
		public CompensateWhen(Flux<BaseTransactionEvent> blow, E... exceptionClazz) {
			this(Propagation.LOCAL,blow,exceptionClazz);
		}

		public void setBlow(Flux<BaseTransactionEvent> blow) {
			this.blow = blow;
		}

		public E[] getExceptions() {
			return exceptionClazz;
		}

		public Propagation getPropagation() {
			return propagation;
		}

		public Flux<BaseTransactionEvent> getBlow() {
			return blow;
		}
	}

	public static class ReceivedCommand<P> {
		private Class<P> target;
		private Function<P,Mono<Void>> consumer;

		public ReceivedCommand(Class<P> target, Function<P,Mono<Void>> consumer) {
			super();
			this.target = target;
			this.consumer = consumer;
			
		}

		public Class<P> getTarget() {
			return target;
		}

		public Function<P,Mono<Void>> getConsumer() {
			return consumer;
		}
	}
    
   public static class GlobalTransactionPipelineBuilder<T extends BaseGlobalTransactionEvent> {
        
    	private T in;
        private Optional<Supplier> process;
        private Stream<BaseTransactionEvent> blow;
        private WorkerMeta meta;
        private ReceivedCommand receivedCommand;
        private CompensateWhen compensateWhen;
         

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

        public <P> GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> receive(Class<P> target, Function<P,Mono<Void>> consumer) {
            this.receivedCommand = new ReceivedCommand<P>( target, consumer);
            return this;
        }
        @SafeVarargs
		public final <E extends Class<? extends Exception>> GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> compensateWhen(Flux<BaseTransactionEvent> blow, E... exceptionClazz) {
            this.compensateWhen = new CompensateWhen<E>(blow, exceptionClazz);
            return this;
        }
        @SafeVarargs
        public final <E extends Class<? extends Exception>> GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder<T> compensateWhen(Propagation propagation,Flux<BaseTransactionEvent> blow, E... exceptionClazz) {
            this.compensateWhen = new CompensateWhen<E>(propagation,blow, exceptionClazz);
            return this;
        }

        public GlobalTransactionStepDefinition<T> build() {
            return new GlobalTransactionStepDefinition<T>(in,blow, meta,receivedCommand,compensateWhen);
        }

		
    }
}

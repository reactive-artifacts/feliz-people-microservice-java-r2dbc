package com.example.demor2dbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition.GlobalTransactionPipelineBuilder;
import com.example.demor2dbc.kermoss.bfm.WorkerMeta;
import com.example.demor2dbc.kermoss.cache.BubbleCache;
import com.example.demor2dbc.kermoss.cache.BubbleMessage;
import com.example.demor2dbc.kermoss.entities.WmGlobalTransaction;
import com.example.demor2dbc.kermoss.service.BusinessFlow;
import com.example.demor2dbc.kermoss.trx.message.CommitGtx;
import com.example.demor2dbc.kermoss.trx.message.StartGtx;
import com.example.demor2dbc.kermoss.trx.services.GlobalTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

//https://stackoverflow.com/questions/48813030/project-reactor-test-never-completes
@MockitoSettings(strictness = Strictness.STRICT_STUBS)

public class GlobalTransactionServiceTest {

	@Mock
	private R2dbcEntityTemplate template;
	@Mock
	private BusinessFlow businessFlow;

	@Spy
	BubbleCache bubbleCache;
	@Mock
	private ReactiveTransactionManager tm;
	@Mock
	private ObjectMapper objectMapper;
	@InjectMocks
	GlobalTransactionService globalTransactionServiceUnderTest;

	@Test
	public void testNewGlobalTx() {
		GlobalTransactionStepDefinition pipeline = pipeLineBuilder().build();
		ReactiveTransaction status = mock(ReactiveTransaction.class);
		doReturn(Mono.just(status)).when(tm).getReactiveTransaction(any());
		doReturn(Mono.empty()).when(tm).commit(status);
		when(template.insert((WmGlobalTransaction)notNull())).thenReturn(Mono.empty());
		when(businessFlow.consumeSafeEvent(notNull())).thenReturn(Mono.empty());
		Mono<Void> beginTx = globalTransactionServiceUnderTest.begin(new StartGtx(pipeline));
		
		StepVerifier
		  .create(beginTx).expectComplete().verify();
		verify(template).insert(any(WmGlobalTransaction.class));
		verify(businessFlow).consumeSafeEvent(same(pipeline.getIn()));
		verify(tm).commit(status);
	}
	
	@Test
	public void testNewGlobalTxAsChild() {
		GlobalTransactionStepDefinition pipeline = pipeLineBuilder().build();
		String pgtx = UUID.randomUUID().toString();
		ReactiveTransaction status = mock(ReactiveTransaction.class);
		doReturn(Mono.just(status)).when(tm).getReactiveTransaction(any());
		doReturn(Mono.empty()).when(tm).commit(status);
		when(bubbleCache.getBubble(pipeline.getIn().getId())).
		thenReturn(Mono.just(BubbleMessage.builder().PGTX(pgtx).build()));
		when(template.insert((WmGlobalTransaction)notNull())).thenReturn(Mono.empty());
		when(businessFlow.consumeSafeEvent(notNull())).thenReturn(Mono.empty());
		Mono<Void> beginTx = globalTransactionServiceUnderTest.begin(new StartGtx(pipeline));
		StepVerifier
		  .create(beginTx).expectComplete().verify();
		ArgumentCaptor<WmGlobalTransaction> wmgCaptor = ArgumentCaptor.forClass(WmGlobalTransaction.class);
		verify(template).insert(wmgCaptor.capture());	
		verify(businessFlow).consumeSafeEvent(same(pipeline.getIn()));
		verify(tm).commit(status);
	
		assertEquals(pgtx,wmgCaptor.getValue().getParent());
	}
	
	
	@Test
	public void testCommitWhenNoEventInBubbleCache() {
		GlobalTransactionStepDefinition pipeline = pipeLineBuilder().build();
		String pgtx = UUID.randomUUID().toString();
		ReactiveTransaction status = mock(ReactiveTransaction.class);
		doReturn(Mono.just(status)).when(tm).getReactiveTransaction(any());
		doReturn(Mono.empty()).when(tm).rollback(status);
		
		Mono<Void> commitTx = globalTransactionServiceUnderTest.commit(new CommitGtx(pipeline));
		StepVerifier
		  .create(commitTx).expectError().verify();
		verify(tm).rollback(status);
	
	}

	private  GlobalTransactionPipelineBuilder pipeLineBuilder() {
		return GlobalTransactionStepDefinition.builder().in(new FakeGlobalTransactionEvent())
				.meta(new WorkerMeta("GlobalTransactionTest"));
	}

}

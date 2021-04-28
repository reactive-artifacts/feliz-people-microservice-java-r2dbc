package com.example.demor2dbc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
//https://stackoverflow.com/questions/48813030/project-reactor-test-never-completes
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ATest {

	@Mock
	B btest;
	@InjectMocks
	A aTest;
	
	@Test
	public void testX() {
       when(btest.getMono(anyString())).thenReturn(Mono.just("A"));
		StepVerifier
		  .create(aTest.x()).expectNext("A","A").expectComplete().verify();
		
		verify(btest,times(2)).getMono(anyString());
	}
	
	@Test
	public void testXWithRealMethod() {
       when(btest.getMono(anyString())).thenCallRealMethod();
		StepVerifier
		  .create(aTest.x()).expectNext("A","B").expectComplete().verify();
		
		verify(btest,times(2)).getMono(anyString());
	}
	
	
	@Test
	// Test Mono void
	public void testY() {
       when(btest.getMono(anyString())).thenCallRealMethod();
		StepVerifier
		  .create(aTest.y()).expectComplete().verify();
		
		verify(btest,times(2)).getMono(anyString());
	}
	
}

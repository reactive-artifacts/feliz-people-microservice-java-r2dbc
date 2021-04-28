package com.example.demor2dbc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import reactor.test.StepVerifier;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ATestCaseWithSpy {

	@Spy
	B btest;
	@InjectMocks
	A aTest;
	
	@Test
	public void testX() {
		
		StepVerifier
		  .create(aTest.x()).expectNext("A","B").expectComplete().verify();
		
		verify(btest,times(2)).getMono(anyString());
	}
	
}

package com.example.demor2dbc.kermoss.trx.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.LocalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.trx.message.CommitLtx;
import com.example.demor2dbc.kermoss.trx.message.StartLtx;

@Aspect
@Component
public class BusinessLocalTransactionAspect {
	
	

	@Autowired
	private ApplicationEventPublisher publisher;

    @Pointcut("@annotation(com.example.demor2dbc.kermoss.trx.annotation.BusinessLocalTransactional)")
    public void localTransactionPointcut(){
    }

    @Pointcut("@annotation(com.example.demor2dbc.kermoss.trx.annotation.SwitchBusinessLocalTransactional)")
    public void moveLocalTransactionPointcut(){
    }
    
    

    @Around("localTransactionPointcut()")
    public void beginLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        LocalTransactionStepDefinition pipeline = (LocalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new StartLtx(pipeline)); 
       
    }

    @Around("moveLocalTransactionPointcut()")
    public void moveLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        LocalTransactionStepDefinition pipeline = (LocalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new CommitLtx(pipeline));
    }
    

}

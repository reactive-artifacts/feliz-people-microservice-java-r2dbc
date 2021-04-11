package com.example.demor2dbc.kermoss.trx.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.bfm.GlobalTransactionStepDefinition;
import com.example.demor2dbc.kermoss.trx.message.CommitGtx;
import com.example.demor2dbc.kermoss.trx.message.StartGtx;


@Aspect
@Component
public class BusinessGlobalTransactionAspect {

    
	@Autowired
	private ApplicationEventPublisher publisher;
	
    @Pointcut("@annotation(com.example.demor2dbc.kermoss.trx.annotation.BusinessGlobalTransactional)")
    public void globalTransactionPointcut(){
    
    }

    @Pointcut("@annotation(com.example.demor2dbc.kermoss.trx.annotation.CommitBusinessGlobalTransactional)")
    public void commitGlobalTransactionPointcut(){
    }


    @Around("globalTransactionPointcut()")
    public void beginLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new StartGtx(pipeline)); 
    }

    @Around("commitGlobalTransactionPointcut()")
    public void moveLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new CommitGtx(pipeline)); 
     
    }
}
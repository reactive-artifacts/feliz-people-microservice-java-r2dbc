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
import com.example.demor2dbc.kermoss.trx.message.RollBackGtx;
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
    @Pointcut("@annotation(com.example.demor2dbc.kermoss.trx.annotation.RollbackBusinessGlobalTransactional)")
    public void rollBackGlobalTransactionPointcut(){
    }


    @Around("globalTransactionPointcut()")
    public void beginGlobalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new StartGtx(pipeline)); 
    }

    @Around("commitGlobalTransactionPointcut()")
    public void commitGlobalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new CommitGtx(pipeline)); 
     
    }
    
    @Around("rollBackGlobalTransactionPointcut()")
    public void rollBackGlobalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        publisher.publishEvent(new RollBackGtx(pipeline));  
    }
}
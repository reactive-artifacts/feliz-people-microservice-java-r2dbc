package com.example.demor2dbc.kermoss.trx.annotation;
import org.springframework.context.event.EventListener;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@EventListener
public @interface BusinessGlobalTransactional {

}
package com.example.demor2dbc;

import java.util.Date;

import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demor2dbc.exceptions.PersonNotFoundException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class RestExceptionHandler {
  /**
   *custom exceptions
   *@https://stackoverflow.com/questions/53595420/correct-way-of-throwing-exceptions-with-reactor 
   */
  @ExceptionHandler(value = {IllegalArgumentException.class,DecodingException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public Mono<ErrorMessage> IllegalArgumentExceptionHandler(Exception ex) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.BAD_REQUEST.value(),
        new Date(),
        ex.getMessage(),
        null);
    
    return Mono.just(message);
  }
  
  
  @ExceptionHandler(value = {PersonNotFoundException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public Mono<ErrorMessage> PersonNotFoundExceptionHandler(Exception ex) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.NOT_FOUND.value(),
        new Date(),
        "Person not found",
        null);
    
    return Mono.just(message);
  }
}
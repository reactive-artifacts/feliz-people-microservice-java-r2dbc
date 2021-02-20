package com.example.demor2dbc.web;

import java.util.Date;

import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demor2dbc.exceptions.ErrorMessage;
import com.example.demor2dbc.exceptions.ForbiddenAccessException;
import com.example.demor2dbc.exceptions.ResourceNotFoundException;
import com.example.demor2dbc.exceptions.UnAuthorizedAccessException;

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
  
  
  @ExceptionHandler(value = {ResourceNotFoundException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public Mono<ErrorMessage> PersonNotFoundExceptionHandler(Exception ex) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.NOT_FOUND.value(),
        new Date(),
        "Resource not found",
        null);
    
    return Mono.just(message);
  }
  
  @ExceptionHandler(value = {UnAuthorizedAccessException.class})
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  public Mono<ErrorMessage> UnAuthorizedAccessExceptionHandler(Exception ex) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.UNAUTHORIZED.value(),
        new Date(),
        "UnAuthorized access",
        null);
    
    return Mono.just(message);
  }
  @ExceptionHandler(value = {ForbiddenAccessException.class})
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  public Mono<ErrorMessage> ForbiddenAccessExceptionHandler(Exception ex) {
	  ErrorMessage message = new ErrorMessage(
			  HttpStatus.FORBIDDEN.value(),
			  new Date(),
			  "Forbidden access",
			  null);
	  
	  return Mono.just(message);
  }
}
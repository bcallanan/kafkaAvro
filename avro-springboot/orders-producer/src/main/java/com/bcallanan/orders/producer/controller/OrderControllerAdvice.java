package com.bcallanan.orders.producer.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class OrderControllerAdvice {
	
	@ExceptionHandler( MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleException( MethodArgumentNotValidException exception) {
		
		var errorMessage = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map( fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
			.sorted()
			.collect( Collectors.joining( "," ));
		
		log.info( "error Message: {}", errorMessage);
		
		return new ResponseEntity<>( errorMessage, HttpStatus.BAD_REQUEST);
	}

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException ex) {

        List<FieldError> errorList = ex.getBindingResult().getFieldErrors();
        String errorMessage = errorList.stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
 
        log.info("errorMessage : {} ", errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}

package com.saiket.myRetail.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyRetailExceptionHandler 
{

	@ExceptionHandler(MyRetailException.class)
	public ResponseEntity<ErrorMessage> processPriceNotFoundException(MyRetailException e)
	{
		return new ResponseEntity<ErrorMessage>( new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RedskyException.class)
	public ResponseEntity<ErrorMessage> processRedskyException(RedskyException e)
	{
		return new ResponseEntity<ErrorMessage>( new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> processOtherExceptions(Exception e)
	{
		return new ResponseEntity<ErrorMessage>( new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
	}
}

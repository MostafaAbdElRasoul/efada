package com.efada.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class EfadaExceptionHandler {

	@ExceptionHandler(EfadaCustomException.class)
	public ResponseEntity<?> handleEfadaCustomException(EfadaCustomException ex){
		log.error("Efada custom exception error >>");
		log.error(ex.getMessage());
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGeneralException(Exception ex) {
		log.error("General exception error >>");
		log.error(ex.getMessage());
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

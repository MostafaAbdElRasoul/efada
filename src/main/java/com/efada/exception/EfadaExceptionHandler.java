package com.efada.exception;

import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.efada.base.BaseResponse;
import com.efada.utils.EfadaLogger;
import com.efada.utils.EfadaUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class EfadaExceptionHandler extends ResponseEntityExceptionHandler{

	@Autowired
    private EfadaUtils efadaUtils;
	
	@Autowired
	private EfadaLogger efadaLogger;
	
	private BaseResponse storeErrorAndReturnResponse(Exception ex, int httpStatusCode, Locale locale, HttpServletRequest request) {
		efadaLogger.printStackTrace(ex, log);
		
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);
		
		efadaUtils.createErrorLogAndErrorFile(ex, request, httpStatusCode);
		
		BaseResponse response = BaseResponse.builder()
				.code(httpStatusCode)
				.errors(Arrays.asList(errorMessage))
				.status(false)
				.build();
		return response;
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<BaseResponse> handleEntityNotFoundException(EntityNotFoundException ex, Locale locale, HttpServletRequest request){
		BaseResponse response = storeErrorAndReturnResponse(ex, HttpStatus.BAD_REQUEST.value(), locale, request);
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<BaseResponse> handleNoSuchElementException(NoSuchElementException ex, Locale locale, HttpServletRequest request){
		log.error("There is no data for your request >> "+ex.getMessage());
		BaseResponse response = storeErrorAndReturnResponse(ex, HttpStatus.BAD_REQUEST.value(), locale, request);
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(EfadaCustomException.class)
	public ResponseEntity<BaseResponse> handleEfadaCustomException(EfadaCustomException ex, Locale locale, HttpServletRequest request){
		log.error("Efada custom exception error >> "+ex.getMessage());
		
		BaseResponse response = storeErrorAndReturnResponse(ex, HttpStatus.BAD_REQUEST.value(), locale, request);
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse> handleGeneralException(Exception ex, Locale locale, HttpServletRequest request) {
		log.error("General exception error >> "+ex.getMessage());
		
		BaseResponse response = storeErrorAndReturnResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), locale, request);
		return new ResponseEntity<BaseResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

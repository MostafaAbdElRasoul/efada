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

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class EfadaExceptionHandler extends ResponseEntityExceptionHandler{

	@Autowired
    private EfadaUtils efadaUtils;
	
	@Autowired
	private EfadaLogger efadaLogger;
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<BaseResponse> handleNoSuchElementException(NoSuchElementException ex, Locale locale, HttpServletRequest request){
		log.error("There is no data for your request >> "+ex.getMessage());
		
		efadaLogger.printStackTrace(ex, log);
		
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);
		
		efadaUtils.createErrorLogAndErrorFile(ex, request, HttpStatus.BAD_REQUEST.value());
		
		BaseResponse response = BaseResponse.builder()
				.code(HttpStatus.BAD_REQUEST.value())
				.errors(Arrays.asList(errorMessage))
				.status(false)
				.build();
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(EfadaCustomException.class)
	public ResponseEntity<BaseResponse> handleEfadaCustomException(EfadaCustomException ex, Locale locale, HttpServletRequest request){
		log.error("Efada custom exception error >> "+ex.getMessage());
		
		efadaLogger.printStackTrace(ex, log);
		
		efadaUtils.createErrorLogAndErrorFile(ex, request, HttpStatus.BAD_REQUEST.value());

		
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);

		BaseResponse response = BaseResponse.builder()
				.code(HttpStatus.BAD_REQUEST.value())
				.errors(Arrays.asList(errorMessage))
				.status(false)
				.build();
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse> handleGeneralException(Exception ex, Locale locale, HttpServletRequest request) {
		log.error("General exception error >> "+ex.getMessage());
		
		efadaLogger.printStackTrace(ex, log);
		
		efadaUtils.createErrorLogAndErrorFile(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());

		
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);
		BaseResponse response = BaseResponse.builder()
				.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.errors(Arrays.asList(errorMessage))
				.status(false)
				.build();
		
		return new ResponseEntity<BaseResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

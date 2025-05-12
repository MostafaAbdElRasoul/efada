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
import com.efada.utils.EfadaUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class EfadaExceptionHandler extends ResponseEntityExceptionHandler{

	@Autowired
    private EfadaUtils efadaUtils;
	
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<BaseResponse> handleNoSuchElementException(NoSuchElementException ex, Locale locale, HttpServletRequest request){
		log.error("There is no data for your request >> "+ex.getMessage());
		ex.printStackTrace();
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);
		
		Long errKey = efadaUtils.createTheErrorLog(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());
		log.error("errKey : "+errKey);
		
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
		ex.printStackTrace();
		
		Long errKey = efadaUtils.createTheErrorLog(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());
		log.error("errKey : "+errKey);
		
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
		ex.printStackTrace();
		
		Long errKey = efadaUtils.createTheErrorLog(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());
		log.error("errKey : "+errKey);
		
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);
		BaseResponse response = BaseResponse.builder()
				.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.errors(Arrays.asList(errorMessage))
				.status(false)
				.build();
		
		return new ResponseEntity<BaseResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

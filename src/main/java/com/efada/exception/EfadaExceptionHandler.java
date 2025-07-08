package com.efada.exception;

import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
	
	private String extractConstraintName(DataIntegrityViolationException ex) {
	    Throwable rootCause = ExceptionUtils.getRootCause(ex);
	    if (rootCause != null && rootCause.getMessage() != null) {
	        String message = rootCause.getMessage();

	        // Match the constraint name after 'key '
	        Pattern pattern = Pattern.compile("key '([^']+)'");
	        Matcher matcher = pattern.matcher(message);
	        if (matcher.find()) {
	            String fullKey = matcher.group(1); // e.g., registrations.UK_REGISTRATION_ATTENDEE_SESSION
	            int dotIndex = fullKey.lastIndexOf('.');
	            return dotIndex != -1 ? fullKey.substring(dotIndex + 1) : fullKey;
	        }
	    }
	    return "unknown_constraint";
	}
	
//	@ExceptionHandler(UsernameNotFoundException.class)
//	public ResponseEntity<BaseResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, Locale locale, HttpServletRequest request){
//		System.out.println("username not found : ");
//		BaseResponse response = storeErrorAndReturnResponse(ex, HttpStatus.UNAUTHORIZED.value(), locale, request);
//		return new ResponseEntity<BaseResponse>(response, HttpStatus.UNAUTHORIZED);
//	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<BaseResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, Locale locale, HttpServletRequest request){
		log.error("Constraint error from the database : ");
		String constraintName = extractConstraintName(ex);
		log.error("constraintName : "+constraintName);
		
		efadaLogger.printStackTrace(ex, log);
		String errorMessage = efadaUtils.getMessageFromMessageSource(constraintName, null, locale);
		
		efadaUtils.createErrorLogAndErrorFile(ex, request, HttpStatus.BAD_REQUEST.value());
		
		BaseResponse response = BaseResponse.builder()
				.code(HttpStatus.BAD_REQUEST.value())
				.errors(Arrays.asList(errorMessage))
				.status(false)
				.build();
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
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
	
	
	@ExceptionHandler(EfadaValidationException.class)
	public ResponseEntity<BaseResponse> handleEfadaValidationException(EfadaValidationException ex, Locale locale, HttpServletRequest request){
		log.error("Efada validation exception error >> "+ex.getMessage());
		
		efadaLogger.printStackTrace(ex, log);
		
		String errorMessage = efadaUtils.getMessageFromMessageSource(ex.getMessage(), null, locale);
		
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
		
		BaseResponse response = storeErrorAndReturnResponse(ex, HttpStatus.BAD_REQUEST.value(), locale, request);
		return new ResponseEntity<BaseResponse>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<BaseResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex, Locale locale, HttpServletRequest request){
		log.error("AuthorizationDeniedException error >> "+ex.getMessage());
		
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

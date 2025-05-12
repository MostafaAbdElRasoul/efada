package com.efada.utils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.efada.entity.ErrorLog;
import com.efada.repository.ErrorLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EfadaUtils {

	private final MessageSource messageSource;
	
	private final ErrorLogRepository errorLogRepository;
	
	public String getMessageFromMessageSource(String message, Object[] args, Locale locale) {
		String msg = "";
		try {
			msg = messageSource.getMessage(message, args, locale);

		}catch(NoSuchMessageException ex) {
			msg = message == null? "": message;
		}
		return msg;
	}
	
	public Long createTheErrorLog(Exception ex, HttpServletRequest request, int statusCode) {
		
		
		String stackTrace = ex.getStackTrace().length > 0
		        ? ex.getStackTrace()[0].toString()
		        : "No stack trace available";
		
		ErrorLog errorLog = ErrorLog.builder()
				.exception(ex.getClass().getName())
				.message(ex.getMessage())
		        .path(request.getRequestURI())
		        .statusCode(statusCode)
		        .timestamp(Instant.now().toString())
		        .stackTrace(stackTrace)
				.build();
		
		errorLogRepository.save(errorLog);
		
		return errorLog.getErKey();
	}

}

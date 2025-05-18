package com.efada.utils;

import java.time.Instant;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.efada.entity.ErrorLog;
import com.efada.repository.ErrorLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EfadaUtils {

	private final MessageSource messageSource;
	
	private final ErrorLogRepository errorLogRepository;
	
	private final FileSystemUtils fileSystemUtils;
	
	public EfadaUtils(MessageSource messageSource, ErrorLogRepository errorLogRepository,
			FileSystemUtils fileSystemUtils) {
		super();
		this.messageSource = messageSource;
		this.errorLogRepository = errorLogRepository;
		this.fileSystemUtils = fileSystemUtils;
	}
	
	
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

	public void createErrorLogAndErrorFile(Exception ex, HttpServletRequest request, int statusCode) {
		Long errKey = createTheErrorLog(ex, request, statusCode);
		log.error("errKey : "+errKey);
		fileSystemUtils.createErrorLogFile(ex, errKey);
	}

	
}

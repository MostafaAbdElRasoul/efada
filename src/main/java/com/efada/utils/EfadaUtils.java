package com.efada.utils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.efada.base.BaseResponse;
import com.efada.entity.ErrorLog;
import com.efada.entity.UserLoginHistory;
import com.efada.enums.LoginAction;
import com.efada.redis.entities.LoggedUser;
import com.efada.repository.ErrorLogRepository;
import com.efada.security.EfadaSecurityUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

	public static void registerUserInSecurityContext(EfadaSecurityUser user) {
		Authentication authentication = new UsernamePasswordAuthenticationToken(user , null , user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	public static LoggedUser createLoggedUserObject(EfadaSecurityUser user ,String token , HttpServletRequest httpRequest) {
		LoggedUser loggedUser = LoggedUser.builder()
				.id(user.getId().toString())
				.userName(user.getUsername())
				.browser(httpRequest.getHeader("User-Agent"))
				.ipAddress(getClientIP(httpRequest))
				.token(token)
				.loginDate(LocalDateTime.now())
				.build();
		return loggedUser;
		
	}
	
	public static UserLoginHistory createUserLoginHistory(EfadaSecurityUser user, LoginAction action, HttpServletRequest httpRequest) {
		UserLoginHistory userloginHistory = UserLoginHistory.builder()
				.userId(user.getId())
				.browser(httpRequest.getHeader("User-Agent"))
				.ipAddress(getClientIP(httpRequest))
				.loginDate(LocalDateTime.now())
				.action(action)
				.build();
		
		return userloginHistory;	
	}
	
	public static String getClientIP(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		String remoteIp = request.getRemoteAddr();

		if (xfHeader == null) {
			return remoteIp;
		}
		String clientIpAddress = xfHeader.split(",")[0];
	
		return clientIpAddress;
	}
	
	public static void sendErrorResponse(HttpServletResponse response, HttpServletRequest request, HttpStatus status,
			List<String> errorMessages) throws IOException {
		ResponseEntity<BaseResponse> errorResponse = buildResponse(null, false, status, errorMessages);
		
		response.setContentType("application/json");
		response.setStatus(status.value());
		new ObjectMapper().writeValue(response.getOutputStream(), errorResponse.getBody());
	}
	
	public static ResponseEntity<BaseResponse> buildResponse(Object data, boolean success, HttpStatus status, List<String> errors) {
        BaseResponse response = BaseResponse.builder()
                .status(success)
                .code(status.value())
                .data(data)
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, status);
    }
}

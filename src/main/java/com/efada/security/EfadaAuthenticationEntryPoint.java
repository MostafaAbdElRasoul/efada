package com.efada.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.efada.base.BaseResponse;
import com.efada.utils.EfadaLogger;
import com.efada.utils.EfadaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EfadaAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private EfadaLogger efadaLogger;

    @Autowired
    private EfadaUtils efadaUtils;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        efadaLogger.printStackTrace(authException, log);
        
        // Handle specific authentication exceptions
        if (authException.getCause() instanceof UsernameNotFoundException) {
            handleUsernameNotFound(response, request, (UsernameNotFoundException) authException.getCause());
            return;
        }
        
        if (authException instanceof BadCredentialsException) {
            handleBadCredentials(response, request, (BadCredentialsException) authException);
            return;
        }
        
        // Default handling for other auth exceptions
        String errorMessage = efadaUtils.getMessageFromMessageSource(
                authException.getMessage(), null, request.getLocale()
        );
        
        EfadaUtils.sendErrorResponse(response, request, 
                         HttpStatus.UNAUTHORIZED, 
                         List.of(errorMessage));
    }

    private void handleUsernameNotFound(HttpServletResponse response, 
                                      HttpServletRequest request,
                                      UsernameNotFoundException ex) throws IOException {
        String errorMessage = efadaUtils.getMessageFromMessageSource(
                ex.getMessage(), 
                null, 
                request.getLocale()
        );
        
        EfadaUtils.sendErrorResponse(response, request, 
                         HttpStatus.UNAUTHORIZED, 
                         List.of(errorMessage));
    }

    private void handleBadCredentials(HttpServletResponse response,
                                    HttpServletRequest request,
                                    BadCredentialsException ex) throws IOException {
        String errorMessage = efadaUtils.getMessageFromMessageSource(
                "BAD_CREDENTIALS", 
                null, 
                request.getLocale()
        );
        
        EfadaUtils.sendErrorResponse(response, request, 
                         HttpStatus.UNAUTHORIZED, 
                         List.of(errorMessage));
    }

}
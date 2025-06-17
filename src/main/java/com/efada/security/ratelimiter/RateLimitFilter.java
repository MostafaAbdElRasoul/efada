package com.efada.security.ratelimiter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.efada.base.BaseResponse;
import com.efada.utils.EfadaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimiterService rateLimiterService;
    private final EfadaUtils efadaUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIP = httpRequest.getRemoteAddr();
        String path = httpRequest.getServletPath();
        String redisKey = clientIP + ":" + path;
        
        log.info("redisKey : "+redisKey);

        if (!rateLimiterService.isAllowed(redisKey)) {
        	log.info("Too many requests for : "+redisKey);
        	String message = efadaUtils.
            		getMessageFromMessageSource("TOO_MANY_REQUESTS", null, httpRequest.getLocale());
            EfadaUtils.sendErrorResponse(httpResponse, httpRequest, 
                    HttpStatus.TOO_MANY_REQUESTS, 
                    List.of(message));
            return;
        }

        chain.doFilter(request, response);
    }
    
    
}

package com.efada.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.efada.security.EfadaSecurityUser;
import com.efada.security.EfadaUserDetailsService;
import com.efada.utils.EfadaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final EfadaUserDetailsService userDetailsService;
    private final EfadaUtils efadaUtils; 

    
    public JwtAuthenticationFilter(JwtService jwtService, 
    		EfadaUserDetailsService userDetailsService,
    		EfadaUtils efadaUtils) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.efadaUtils = efadaUtils;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip filter for refresh token endpoint
        if (request.getRequestURI().equals("/api/v1/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        System.out.println("jwt : "+jwt);

        // Reject refresh tokens used as access tokens
        if (jwtService.isRefreshToken(jwt)) {
        	System.out.println("refresh : ");
        	 sendErrorResponse(request, response,
                     HttpServletResponse.SC_UNAUTHORIZED, "REFRESH_TOKEN_FOR_AUTHENTICATION");
            return;
        }
        
        username = jwtService.extractUsername(jwt);
        System.out.println("username : "+username);
        
        
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	EfadaSecurityUser userDetails = (EfadaSecurityUser) this.userDetailsService.loadUserByUsername(username);
        	System.out.println("userDetails : "+userDetails);
        	if (jwtService.isTokenValid(jwt, userDetails)) {
        		System.out.println("token valid : ");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    
    private void sendErrorResponse(HttpServletRequest request, 
            HttpServletResponse response,
            int statusCode,
            String messageCode) throws IOException {
    	
    	String errorMessage = efadaUtils.getMessageFromMessageSource(messageCode, null, request.getLocale());
        System.out.println("err "+errorMessage);
		
		// Set response content type
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(statusCode);
		
		// Create JSON error response
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", statusCode);
		body.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
		body.put("message", errorMessage);
		body.put("path", request.getServletPath());
		
		// Write response
	    try (java.io.OutputStream out = response.getOutputStream()) {
	        new ObjectMapper().writeValue(out, body);
	    }
	}
}

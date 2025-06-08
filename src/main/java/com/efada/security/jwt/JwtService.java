package com.efada.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.efada.security.EfadaSecurityUser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String secretKey = "EfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKey";
    // Token type constants
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    
    
    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
//    public String extractUsername(String token) {
//        return extractClaim(token, claims -> claims.get("username", String.class)); // ✅ correct for custom claim
//    }

    public String generateAccessToken(EfadaSecurityUser userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);
     // Extract the first authority as a string
        String authority = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ATENDEE");
        claims.put("authorities", authority);
        // Add any additional user-specific claims here

        return buildToken(claims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(EfadaSecurityUser userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);
        
        return buildToken(claims, userDetails, refreshExpiration);
    }
    
    public boolean isRefreshToken(String token) {
	    try {
	    	final Claims claims = extractAllClaims(token);
	        return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM));
	    }catch (ExpiredJwtException ex) {
	    	return REFRESH_TOKEN_TYPE.equals(ex.getClaims().get(TOKEN_TYPE_CLAIM));
		}
        
    }

    public boolean isAccessToken(String token) {
        final Claims claims = extractAllClaims(token);
        return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(EfadaSecurityUser userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails,
        int expiration
    ) {
    	Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, expiration);
		
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(calendar.getTime())
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, EfadaSecurityUser userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public boolean isRefreshTokenExpired(String token) {
        try {
            extractAllClaims(token);
            return false;
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
    
    public boolean isValidRefreshToken(String token) {
        try {
            return isRefreshToken(token) && 
                   !isTokenExpired(token) && 
                   extractAllClaims(token) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
}
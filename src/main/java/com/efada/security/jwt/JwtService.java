package com.efada.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.efada.security.EfadaSecurityUser;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String secretKey = "EfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKeyEfadaSecKey";
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
//    public String extractUsername(String token) {
//        return extractClaim(token, claims -> claims.get("username", String.class)); // ✅ correct for custom claim
//    }

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

    public String generateRefreshToken(EfadaSecurityUser userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails,
        long expiration
    ) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
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
    	System.out.println("extractAllClaims > "+token);
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
    
//    public String createToken(ObjectNode tokenBodyData) {
//    	System.out.println("tokenBodyData "+tokenBodyData);
//    	
//		String token = Jwts.builder()
//				           .addClaims(extractAllClaims(tokenBodyData.toString()))
//				           .setIssuedAt(new Date())
//				           .setExpiration(new Date(System.currentTimeMillis()+(jwtExpiration)))
//				           .signWith(getSignInKey())
//				           .compact();
//		
//		return "Bearer "+ token;
//	}
    
    public String createToken(ObjectNode tokenBodyData) {
        System.out.println("tokenBodyData: " + tokenBodyData);

        // Convert ObjectNode to Map<String, Object>
        Map<String, Object> claims = new HashMap<>();
        tokenBodyData.fields().forEachRemaining(entry -> claims.put(entry.getKey(), entry.getValue().asText()));

        String token = Jwts.builder()
                .addClaims(claims)
                .setSubject(tokenBodyData.get("username").asText())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        return "Bearer " + token;
    }
}
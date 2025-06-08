package com.efada.serviceImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.efada.base.BaseResponse;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.enums.UserRole;
import com.efada.exception.EfadaCustomException;
import com.efada.repository.AppUserRepository;
import com.efada.security.EfadaSecurityUser;
import com.efada.security.jwt.JwtService;
import com.efada.utils.EfadaUtils;
import com.efada.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final AppUserRepository appUserRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	public BaseResponse login(ObjectNode authenticationData) {
		AppUser user = appUserRepository
				.findByUsernameIgnoreCase(authenticationData.get("username").asText())
				.orElseThrow( ()-> new UsernameNotFoundException("USERNAME_NOT_FOUND"));
		if(user != null) {
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					authenticationData.get("username").asText(), authenticationData.get("password").asText());
			
			authenticationManager.authenticate(authentication);
			
			return generateAuthenticationResponse(user);
		}
		
		
		throw new BadCredentialsException("BAD_CREDENTIALS");
	}
	
	
	private BaseResponse generateAuthenticationResponse (AppUser userData) {
    	
		AppUserDTO userDataDTO = ObjectMapperUtils.map(userData, AppUserDTO.class);
		EfadaSecurityUser  authenticationUser = createAuthenticationUserObject(userDataDTO);
		
//		LoggedUser loggedUser = IthmaarCommon.createLoggedUserObject(authenticationUser, token, httpRequest);
//		loggedUserRepository.save(loggedUser);
		
//		saveLoginHistory(customerDataDTO.getBpKey());
		
//		int statusCode = action.equals("register")?HttpStatus.CREATED.value():HttpStatus.OK.value();
	
//		String messageSourceCode = action.equals("register")?"REGISTRATION_COMPLETE_MSG":"SUCCESS_LOGIN_MSG";
//		String message = messageSource.getMessage(messageSourceCode, new Object[] {}, new Locale(HttpRequestMetaData.language));
		
		// Generate both access and refresh tokens
        String accessToken = jwtService.generateAccessToken(authenticationUser);
        String refreshToken = jwtService.generateRefreshToken(authenticationUser);
        
        // Register user in security context
        EfadaUtils.registerUserInSecurityContext(authenticationUser);
        
        // Prepare token map for response
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("tokenType", "Bearer");
        
		BaseResponse response = BaseResponse.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .data(userDataDTO)
                .tokens(tokens)
                .build();
		
         return response;
	}
	
	private EfadaSecurityUser createAuthenticationUserObject(AppUserDTO userDataDTO) {
		
		Collection<? extends GrantedAuthority> authorities = Arrays.asList(
				      new SimpleGrantedAuthority(
				    		  userDataDTO.getRole().equals(UserRole.ADMIN)?"ADMIN":
				    			  userDataDTO.getRole().equals(UserRole.SPEAKER)? "SPEAKER":"ATENDEE"));
		
		EfadaSecurityUser authenticationUser = new EfadaSecurityUser(userDataDTO.getUsername()
				, userDataDTO.getPassword(), authorities,
				userDataDTO.getRole().toString(), userDataDTO.getId());
		
		return authenticationUser;
	} 
	
	public BaseResponse refreshToken(String refreshToken) {
        try {
            // Validate and clean refresh token
            String cleanedRefreshToken = validateAndCleanRefreshToken(refreshToken);
            
            // Verify token validity and type
            if (!jwtService.isValidRefreshToken(cleanedRefreshToken)) {
                throw new EfadaCustomException("INVALID_REFRESH_TOKEN");
            }
            
            return generateResponeForRefreshToken(cleanedRefreshToken);
            
        } catch (Exception e) {
            System.out.println("Refresh token error: {}"+e.getMessage());
            throw new EfadaCustomException("INVALID_REFRESH_TOKEN");
        }
    }
    
    private BaseResponse generateResponeForRefreshToken(String cleanedRefreshToken) {
    	// Extract user details
        String username = jwtService.extractUsername(cleanedRefreshToken);
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("USERNAME_NOT_FOUND"));
        
        // Create authentication user object
        EfadaSecurityUser userDetails = createAuthenticationUserObject(
                ObjectMapperUtils.map(user, AppUserDTO.class));
        
        // Generate new tokens (with refresh token rotation)
        Map<String, String> tokens = generateNewTokens(userDetails);
        
        return BaseResponse.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .tokens(tokens)
                .build();
	}


	private String validateAndCleanRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new EfadaCustomException("REFRESH_TOKEN_IS_REQUIRED");
        }
        return refreshToken.startsWith("Bearer ") ? 
               refreshToken.substring(7) : refreshToken;
    }
    
    private Map<String, String> generateNewTokens(EfadaSecurityUser userDetails) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtService.generateAccessToken(userDetails));
        tokens.put("refreshToken", jwtService.generateRefreshToken(userDetails));
        tokens.put("tokenType", "Bearer");
        return tokens;
    }
   


}

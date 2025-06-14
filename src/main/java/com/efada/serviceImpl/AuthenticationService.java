package com.efada.serviceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.efada.base.BaseResponse;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.entity.UserLoginHistory;
import com.efada.enums.LoginAction;
import com.efada.enums.UserRole;
import com.efada.exception.EfadaCustomException;
import com.efada.redis.entities.LoggedUser;
import com.efada.redis.repositories.LoggedUserRepository;
import com.efada.repository.AppUserRepository;
import com.efada.repository.UserLoginHistoryRepository;
import com.efada.security.EfadaSecurityUser;
import com.efada.security.jwt.JwtService;
import com.efada.utils.EfadaUtils;
import com.efada.utils.EmailService;
import com.efada.utils.OTPUtils;
import com.efada.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EfadaUtils efadaUtils;
	private final AppUserRepository appUserRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final LoggedUserRepository loggedUserRepository;
	private final HttpServletRequest request;
	private final UserLoginHistoryRepository userLoginHistoryRepository;
	private final OTPUtils otpUtils;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;

	
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
		
		
//		int statusCode = action.equals("register")?HttpStatus.CREATED.value():HttpStatus.OK.value();
	
//		String messageSourceCode = action.equals("register")?"REGISTRATION_COMPLETE_MSG":"SUCCESS_LOGIN_MSG";
//		String message = messageSource.getMessage(messageSourceCode, new Object[] {}, new Locale(HttpRequestMetaData.language));
		
		// Generate both access and refresh tokens
        String accessToken = jwtService.generateAccessToken(authenticationUser);
        String refreshToken = jwtService.generateRefreshToken(authenticationUser);
        
		LoggedUser loggedUser = EfadaUtils.createLoggedUserObject(authenticationUser, accessToken, request);
		loggedUserRepository.save(loggedUser);
		
		UserLoginHistory userLoginHistory = EfadaUtils.createUserLoginHistory(authenticationUser, LoginAction.IN, request);
		userLoginHistoryRepository.save(userLoginHistory);
        
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
        String accessToken = jwtService.generateAccessToken(userDetails);
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", jwtService.generateRefreshToken(userDetails));
        tokens.put("tokenType", "Bearer");
        
        LoggedUser loggedUser = EfadaUtils.createLoggedUserObject(userDetails, accessToken, request);
		loggedUserRepository.save(loggedUser);
		
		UserLoginHistory userLoginHistory = EfadaUtils.createUserLoginHistory(userDetails, LoginAction.IN, request);
		userLoginHistoryRepository.save(userLoginHistory);
        return tokens;
    }


    public ResponseEntity<BaseResponse> forgotPassword(String email) {
        Optional<AppUser> optionalUser = appUserRepository.findByEmailIgnoreCase(email);

        if (optionalUser.isEmpty()) {
            return buildResponse(efadaUtils.getMessageFromMessageSource("EMAIL_NOT_FOUND", null, request.getLocale()),
            		false, HttpStatus.BAD_REQUEST);
        }

        int otp = otpUtils.generateOTP(email);
        emailService.sendEmail(email, efadaUtils.getMessageFromMessageSource("OTP_EMAIL_SUBJECT", null, request.getLocale()),
        		efadaUtils.getMessageFromMessageSource("OTP_EMAIL_BODY", null, request.getLocale()) + otp);

        return buildResponse(efadaUtils.getMessageFromMessageSource("OTP_SENT_TO_EMAIL", null, request.getLocale()), true, HttpStatus.OK);
    }

    public ResponseEntity<BaseResponse> resetPassword(String email, Integer otp, String newPassword) {
        if (!otpUtils.verifyOTP(email, otp)) {
            return buildResponse(efadaUtils.getMessageFromMessageSource("INVALID_OR_EXPIRED_OTP", null, request.getLocale()),
            		false, HttpStatus.BAD_REQUEST);
        }

        AppUser user = appUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EfadaCustomException("EMAIL_NOT_FOUND"));

        user.setPassword(passwordEncoder.encode(newPassword)); // Use encoding!
        appUserRepository.save(user);

        return buildResponse(efadaUtils.getMessageFromMessageSource("PASSWORD_RESET_SUCCESS", null, request.getLocale())
        		, true, HttpStatus.OK);
    }

    private ResponseEntity<BaseResponse> buildResponse(String message, boolean success, HttpStatus status) {
        BaseResponse response = BaseResponse.builder()
                .status(success)
                .code(status.value())
                .data(message)
                .build();
        return new ResponseEntity<>(response, status);
    }

}

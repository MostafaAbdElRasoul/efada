package com.efada.serviceImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

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
	
	
	private BaseResponse generateAuthenticationResponse (Object userData) {
    	
		AppUserDTO userDataDTO = ObjectMapperUtils.map(userData, AppUserDTO.class);
		EfadaSecurityUser  authenticationUser = createAuthenticationUserObject(userDataDTO);

		ObjectNode tokenBodyData = getTokenBodyData(userDataDTO);
		String token = jwtService.createToken(tokenBodyData);
		System.out.println("generateAuthenticationResponse "+token);
		EfadaUtils.registerUserInSecurityContext(authenticationUser);
		
//		LoggedUser loggedUser = IthmaarCommon.createLoggedUserObject(authenticationUser, token, httpRequest);
//		loggedUserRepository.save(loggedUser);
		
//		saveLoginHistory(customerDataDTO.getBpKey());
		
//		int statusCode = action.equals("register")?HttpStatus.CREATED.value():HttpStatus.OK.value();
	
//		String messageSourceCode = action.equals("register")?"REGISTRATION_COMPLETE_MSG":"SUCCESS_LOGIN_MSG";
//		String message = messageSource.getMessage(messageSourceCode, new Object[] {}, new Locale(HttpRequestMetaData.language));
		BaseResponse response = BaseResponse.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .data(userDataDTO)
                .token(token)
                .build();
		
         return response;
	}
	
	private ObjectNode getTokenBodyData(AppUserDTO userData) {
		ObjectMapper mapper = new ObjectMapper();
		String authorities = userData.getRole().equals(UserRole.ADMIN)?"ADMIN":
			userData.getRole().equals(UserRole.SPEAKER)? "SPEAKER":"ATENDEE";
	    ObjectNode tokenBodyData = mapper.createObjectNode();
	    tokenBodyData.put("id", userData.getId());
	    tokenBodyData.put("username", userData.getUsername());
	    tokenBodyData.put("type", userData.getRole().toString());
	    tokenBodyData.put("authorities", authorities);
	    return tokenBodyData;
	}
	
	
	private EfadaSecurityUser createAuthenticationUserObject(AppUserDTO userDataDTO) {
		
		Collection<? extends GrantedAuthority> authorities = Arrays.asList(
				      new SimpleGrantedAuthority(
				    		  userDataDTO.getRole().equals("ADMIN")?"ADMIN":
				    			  userDataDTO.getRole().equals("SPEAKER")? "SPEAKER":"ATENDEE"));
		
		EfadaSecurityUser authenticationUser = new EfadaSecurityUser(userDataDTO.getUsername()
				, userDataDTO.getPassword(), authorities,
				userDataDTO.getRole().toString(), userDataDTO.getId());
		return authenticationUser;
	} 
}

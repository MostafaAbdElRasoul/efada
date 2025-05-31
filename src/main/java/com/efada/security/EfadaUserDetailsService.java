package com.efada.security;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.efada.entity.AppUser;
import com.efada.enums.UserRole;
import com.efada.repository.AppUserRepository;


@Service
public class EfadaUserDetailsService implements UserDetailsService{

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user;
		Optional<AppUser> userOptional = appUserRepository.findByUsernameIgnoreCase(username);
		if(userOptional.isPresent())
		{
			user = userOptional.get();
			List<GrantedAuthority> authoroties;
			
			if (UserRole.ADMIN.equals(user.getRole())) {
				authoroties  = Arrays.asList(new SimpleGrantedAuthority(UserRole.ADMIN.toString()));
			}else if(UserRole.SPEAKER.equals(user.getRole())) {
				authoroties  = Arrays.asList(new SimpleGrantedAuthority(UserRole.SPEAKER.toString()));
			}else {
				authoroties  = Arrays.asList(new SimpleGrantedAuthority(UserRole.ATTENDEE.toString()));
			}
			
			EfadaSecurityUser securityUser = 
					new EfadaSecurityUser(user.getUsername(),user.getPassword(),
							authoroties,user.getRole().toString(),user.getId());
			return securityUser;
		}
		throw new UsernameNotFoundException("USERNAME_NOT_FOUND");
	}

}

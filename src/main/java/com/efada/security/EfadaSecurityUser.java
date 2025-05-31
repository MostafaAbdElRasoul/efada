package com.efada.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.efada.entity.AppUser;

public class EfadaSecurityUser extends User{

	public EfadaSecurityUser(String username, String password,
			Collection<? extends GrantedAuthority> authorities, String userRole, Long id) 
	{
		super(username, password, true, true, true, true, authorities);
		this.passwordHash = password;
		this.userRole = userRole;
		this.id = id;
	}
	private String passwordHash;
	private String userRole;
	private Long id;

}

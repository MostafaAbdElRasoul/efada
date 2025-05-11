package com.efada.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.enums.UserRole;
import com.efada.serviceImpl.AppUserServiceImpl;

@Component
public class LineRunner implements CommandLineRunner{

	@Autowired
	private AppUserServiceImpl appUserServiceImpl;
	
	@Override
	public void run(String... args) throws Exception {
		
		AppUserDTO user = new AppUserDTO();
		user.setUsername("mostafa");
		user.setPassword("1234");
		user.setRole(UserRole.ADMIN);
		user.setEmail("momo");
		
		appUserServiceImpl.save(user);
		
	}

}

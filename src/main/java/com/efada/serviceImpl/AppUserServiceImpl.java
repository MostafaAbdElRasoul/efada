package com.efada.serviceImpl;

import org.springframework.stereotype.Service;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;

@Service
public class AppUserServiceImpl extends BaseServiceImpl<AppUser, Long, AppUserDTO>{

	@Override
	public AppUser getEntity() {
		// TODO Auto-generated method stub
		return new AppUser();
	}

	@Override
	public AppUserDTO getDTO() {
		// TODO Auto-generated method stub
		return new AppUserDTO();
	}

}

package com.efada.serviceImpl;

import org.springframework.stereotype.Service;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.RegistrationDTO;
import com.efada.entity.Registration;

@Service
public class RegistrationServiceImpl extends BaseServiceImpl<Registration, Long, RegistrationDTO>{

	@Override
	public Registration getEntity() {
		// TODO Auto-generated method stub
		return new Registration();
	}

	@Override
	public RegistrationDTO getDTO() {
		// TODO Auto-generated method stub
		return new RegistrationDTO();
	}

}

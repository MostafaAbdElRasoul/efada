package com.efada.serviceImpl;

import org.springframework.stereotype.Service;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.SessionDTO;
import com.efada.entity.Session;

@Service
public class SessionServiceImpl extends BaseServiceImpl<Session, Long, SessionDTO>{

	@Override
	public Session getEntity() {
		// TODO Auto-generated method stub
		return new Session();
	}

	@Override
	public SessionDTO getDTO() {
		// TODO Auto-generated method stub
		return new SessionDTO();
	}

}

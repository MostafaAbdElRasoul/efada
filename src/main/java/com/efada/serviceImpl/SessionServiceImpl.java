package com.efada.serviceImpl;

import org.springframework.stereotype.Service;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.SessionDTO;
import com.efada.entity.Session;
import com.efada.repository.SessionRepository;

@Service
public class SessionServiceImpl extends BaseServiceImpl<Session, Long, SessionDTO, SessionRepository>{

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

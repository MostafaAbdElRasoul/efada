package com.efada.serviceImpl;

import org.springframework.stereotype.Service;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.ConferenceDTO;
import com.efada.entity.Conference;
import com.efada.repository.ConferenceRepository;

@Service
public class ConferenceServiceImpl extends BaseServiceImpl<Conference, Long, ConferenceDTO, ConferenceRepository>{

	@Override
	public Conference getEntity() {
		// TODO Auto-generated method stub
		return new Conference();
	}

	@Override
	public ConferenceDTO getDTO() {
		// TODO Auto-generated method stub
		return new ConferenceDTO();
	}

}

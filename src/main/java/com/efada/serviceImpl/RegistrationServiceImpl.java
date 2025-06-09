package com.efada.serviceImpl;

import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.efada.base.BaseResponse;
import com.efada.base.BaseServiceImpl;
import com.efada.dto.CreateRegistrationDTO;
import com.efada.dto.RegistrationDTO;
import com.efada.entity.AppUser;
import com.efada.entity.Registration;
import com.efada.entity.Session;
import com.efada.exception.EfadaCustomException;
import com.efada.repository.AppUserRepository;
import com.efada.repository.RegistrationRepository;
import com.efada.repository.SessionRepository;
import com.efada.utils.ObjectMapperUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RegistrationServiceImpl extends BaseServiceImpl<Registration, Long, RegistrationDTO, RegistrationRepository>{

	
	private final AppUserRepository appUserRepository;
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
	
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

	public RegistrationDTO insert(CreateRegistrationDTO createRegistrationDTO) {
		AppUser attendee = appUserRepository.findById(createRegistrationDTO.getAttendeeId())
	            .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND"));

        Session session = sessionRepository.findById(createRegistrationDTO.getSessionId())
            .orElseThrow(() -> new EntityNotFoundException("SESSION_NOT_FOUND"));

        //replaced with the unique constraint
//        boolean exists = registrationRepository.existsByAttendee_IdAndSession_Id(createRegistrationDTO.getAttendeeId(), createRegistrationDTO.getSessionId());
//        if (exists) 
//            throw new EfadaCustomException("UK_REGISTRATION_ATTENDEE_SESSION");
        
        Registration registration = new Registration();
        registration.setAttendee(attendee);
        registration.setSession(session);
        registration.setRegisteredAt(Instant.now());

        Registration registrationAfterSaving = registrationRepository.save(registration);

        return ObjectMapperUtils.map(registrationAfterSaving, RegistrationDTO.class);
	}

}

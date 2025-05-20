package com.efada.repository;

import org.springframework.stereotype.Repository;

import com.efada.base.BaseRepository;
import com.efada.entity.AppUser;
import com.efada.entity.Registration;
import com.efada.entity.Session;

@Repository
public interface RegistrationRepository extends BaseRepository<Registration, Long>{

//	boolean existsByAttendeeAndSession(AppUser attendee, Session session);
//	boolean existsByAttendee_IdAndSession_Id(Long attendeeId, Long sessionId);
}

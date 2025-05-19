package com.efada.classes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.efada.dto.AppUserDTO;
import com.efada.dto.ConferenceDTO;
import com.efada.dto.RegistrationDTO;
import com.efada.dto.SessionDTO;
import com.efada.entity.AppUser;
import com.efada.enums.SessionStatus;
import com.efada.enums.UserRole;
import com.efada.serviceImpl.AppUserServiceImpl;
import com.efada.serviceImpl.ConferenceServiceImpl;
import com.efada.serviceImpl.RegistrationServiceImpl;
import com.efada.serviceImpl.SessionServiceImpl;

@Component
public class LineRunner implements CommandLineRunner{

	@Autowired
	private AppUserServiceImpl appUserServiceImpl;
	@Autowired
	private SessionServiceImpl sessionServiceImpl;
	@Autowired
	private RegistrationServiceImpl registrationServiceImpl;
	@Autowired
	private ConferenceServiceImpl conferenceServiceImpl;
	
	@Override
	public void run(String... args) throws Exception {
		
		AppUserDTO user = new AppUserDTO();
		user.setUsername("admin");
		user.setPassword("1234");
		user.setRole(UserRole.ADMIN);
		user.setEmail("admin@");
		
		AppUserDTO speaker = new AppUserDTO();
		speaker.setUsername("speaker");
		speaker.setPassword("1234");
		speaker.setRole(UserRole.SPEAKER);
		speaker.setEmail("speaker@");
		
		AppUserDTO attendee = new AppUserDTO();
		attendee.setUsername("attendee");
		attendee.setPassword("1234");
		attendee.setRole(UserRole.ATTENDEE);
		attendee.setEmail("attendee@");
		
		
		user = appUserServiceImpl.save(user);
		speaker = appUserServiceImpl.save(speaker);
		attendee = appUserServiceImpl.save(attendee);
		
		
		ConferenceDTO conference = new ConferenceDTO();
		conference.setTitle("Efada 2025");
		conference.setDescription("Software engineering topics");
		conference.setStartDate(LocalDate.now());
		conference.setEndDate(LocalDate.now().plusDays(20));
		conference.setLocation("Egypt Menia");
		conference = conferenceServiceImpl.save(conference);
		
		SessionDTO javaSession = new SessionDTO();
		javaSession.setTitle("Java");
		javaSession.setConference(conference);
		javaSession.setSpeaker(speaker);
		javaSession.setStatus(SessionStatus.PENDING);
		javaSession = sessionServiceImpl.save(javaSession);
		
		SessionDTO cSession = new SessionDTO();
		cSession.setTitle("C++");
		cSession.setConference(conference);
		cSession.setSpeaker(speaker);
		cSession.setStatus(SessionStatus.APPROVED);
		cSession = sessionServiceImpl.save(cSession);
				
		RegistrationDTO registration = new RegistrationDTO();
		registration.setAttendee(attendee);
		registration.setSession(javaSession);
		registration.setRegisteredAt(Instant.now());
		registration = registrationServiceImpl.save(registration);
		
//		speaker.setSessions(List.of(javaSession));
//		attendee.setRegistrations(List.of(registration));
		
//		conference.setSessions(List.of(javaSession));
		
		
		
		
		
	}

}

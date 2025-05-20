package com.efada.dto;

import java.time.LocalDateTime;

import com.efada.entity.AppUser;
import com.efada.entity.Conference;
import com.efada.enums.SessionStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionDTO {
	
	private Long id;

    private String title;

    private String description;

    private SessionStatus status;

    private LocalDateTime startTime;
    
    private LocalDateTime endTime;

    private String resourceUrl;

    private AppUserDTO speaker;

    private ConferenceDTO conference;

//    private List<RegistrationDTO> registrations;
}

package com.efada.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.efada.entity.AppUser;
import com.efada.entity.Conference;
import com.efada.entity.Registration;
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

    private AppUser speaker;

    private Conference conference;

    private List<Registration> registrations;
}

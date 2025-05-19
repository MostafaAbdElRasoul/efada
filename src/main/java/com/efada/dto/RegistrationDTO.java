package com.efada.dto;


import java.time.Instant;

import com.efada.entity.AppUser;
import com.efada.entity.Session;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationDTO {
	
	private Long id;

    private AppUserDTO attendee;

    private SessionDTO session;

    private Instant registeredAt;

}

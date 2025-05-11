package com.efada.dto;


import java.util.List;

import com.efada.entity.Registration;
import com.efada.entity.Session;
import com.efada.enums.UserRole;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppUserDTO {

	private Long id;

    private String username;

    private String email;

    private String password;

    private UserRole role;

    private String profilePicture;
    
    private List<Session> sessions;

    private List<Registration> registrations;
}

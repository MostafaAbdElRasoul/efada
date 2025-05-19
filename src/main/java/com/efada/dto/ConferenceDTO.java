package com.efada.dto;


import java.time.LocalDate;
import java.util.List;

import com.efada.entity.Session;

import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConferenceDTO {

	private Long id;

    private String title;
    
    private String description;
    
    private String location;
    
    private LocalDate startDate;
    
    private LocalDate endDate;

//    private List<SessionDTO> sessions;
}

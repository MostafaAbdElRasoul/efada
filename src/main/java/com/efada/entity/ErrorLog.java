package com.efada.entity;


import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "ERROR_LOGS")
public class ErrorLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long erKey;
	
	private String exception;
    private String message;
    private String path;
    private int statusCode;

    
    private String stackTrace;

    private String timestamp;
}

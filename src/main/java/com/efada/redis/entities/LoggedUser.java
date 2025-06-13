package com.efada.redis.entities;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@RedisHash("LoggedUser")
public class LoggedUser {
	
	private String id;
	private String userName;
	private String token;
	private String ipAddress;
	private String browser;
	private LocalDateTime loginDate;
}

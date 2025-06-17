package com.efada.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EfadaValidationException extends RuntimeException{

	private String message;
	
	public EfadaValidationException(String message) {
		super(message);
		this.message = message;
	}

}

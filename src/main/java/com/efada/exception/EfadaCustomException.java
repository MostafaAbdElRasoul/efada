package com.efada.exception;

public class EfadaCustomException extends RuntimeException{

	private String errorMessage;
	
	public EfadaCustomException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
}

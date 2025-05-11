package com.efada.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class EfadaCustomException extends RuntimeException{

	private String errorMessage;
}

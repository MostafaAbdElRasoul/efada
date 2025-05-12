package com.efada.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EfadaUtils {

	private final MessageSource messageSource;
	
	public String getMessageFromMessageSource(String message, Object[] args, Locale locale) {
		String msg = "";
		try {
			msg = messageSource.getMessage(message, args, locale);

		}catch(NoSuchMessageException ex) {
			msg = message == null? "": message;
		}
		return msg;
	}
}

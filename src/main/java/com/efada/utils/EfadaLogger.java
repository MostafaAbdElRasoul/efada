package com.efada.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EfadaLogger {

	@Value("${show.trace:false}")
	private Boolean isShowTraceEnabled;
			
	public void printStackTrace(Exception ex, org.slf4j.Logger log) {
		log.debug("isShowTraceEnabled : "+isShowTraceEnabled);
		
		if (isShowTraceEnabled) 
            log.error("Exception occurred: {}: {}", ex.getClass().getName(), ex.getMessage(), ex); // full trace
        else 
            log.error("Exception occurred: {}: {}", ex.getClass().getName(), ex.getMessage());
        
	    
	}	

}

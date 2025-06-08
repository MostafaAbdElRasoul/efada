package com.efada.base;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
	
	@Builder.Default // only works when you initialize the field inline 
    private String timestamp = Instant.now().toString();
    private Boolean status;
    private Map<String, String> tokens;
    private int code;
    private String message;
    private T data;
    private List<String> errors;
}

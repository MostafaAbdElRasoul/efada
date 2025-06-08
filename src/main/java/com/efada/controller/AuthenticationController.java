package com.efada.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.efada.base.BaseResponse;
import com.efada.serviceImpl.AuthenticationService;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Operations related to Authtentication")
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	
	@PostMapping
	@Operation(summary = "Login", description = "Returns the created token")
	public ResponseEntity<BaseResponse> login(@RequestBody ObjectNode authenticationData){
		BaseResponse response = authenticationService.login(authenticationData);
		return new ResponseEntity(response, HttpStatus.OK);
	}
}

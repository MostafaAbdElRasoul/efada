package com.efada.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.efada.base.BaseController;
import com.efada.base.BaseResponse;
import com.efada.dto.CreateRegistrationDTO;
import com.efada.dto.RegistrationDTO;
import com.efada.entity.Registration;
import com.efada.serviceImpl.RegistrationServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/registrations")
@Tag(name = "Registrations", description = "Operations related to registrations")
public class RegistrationController extends BaseController<Long, RegistrationDTO, RegistrationServiceImpl>{

	@Override
	public ResponseEntity<BaseResponse<RegistrationDTO>> insert(RegistrationDTO dto) {
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/custom")
	@Operation(summary = "Insert", description = "Returns the created object")
	public ResponseEntity<BaseResponse<RegistrationDTO>> insert(@RequestBody CreateRegistrationDTO createRegistrationDTO) {
		
	BaseResponse<RegistrationDTO> response = BaseResponse.<RegistrationDTO>builder()
			.data(baseServiceImpl.insert(createRegistrationDTO))
			.code(HttpStatus.CREATED.value())
			.status(true)
			.build();
	
		return new ResponseEntity(response, HttpStatus.CREATED);
	}

	
}

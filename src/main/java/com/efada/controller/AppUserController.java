package com.efada.controller;

import java.awt.PageAttributes.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.efada.base.BaseController;
import com.efada.base.BaseResponse;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.serviceImpl.AppUserServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operations related to users")
public class AppUserController extends BaseController<Long, AppUserDTO, AppUserServiceImpl>{

	
	
	@PutMapping("/{id}/image")
	public ResponseEntity<BaseResponse> changeUserProfileImgae(@RequestParam MultipartFile file,
			@PathVariable Long id){
		byte[] fileBytes= baseServiceImpl.changeUserProfileImgae(file, id);
		
		BaseResponse response = BaseResponse.builder()
				.data(fileBytes)
				.code(HttpStatus.OK.value())
				.status(true)
				.build();
		
		return new ResponseEntity<BaseResponse>(response, HttpStatus.OK);
		
	}
	
	@GetMapping("/{id}/image")
	public ResponseEntity<BaseResponse> getUserProfileImgae(@PathVariable Long id){
		byte[] fileBytes= baseServiceImpl.getUserProfileImgae(id);
		
		BaseResponse response = BaseResponse.builder()
				.data(fileBytes)
				.code(HttpStatus.OK.value())
				.status(true)
				.build();
		
		return new ResponseEntity<BaseResponse>(response, HttpStatus.OK);
		
	} 
}

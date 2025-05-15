package com.efada.base;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.efada.entity.AppUser;
import com.efada.serviceImpl.AppUserServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseController<ID, DTO, S extends BaseServiceImpl>{
	
	@Autowired
	private S baseServiceImpl;
	
	@GetMapping
	@Operation(summary = "Get all", description = "Returns all object")
	public ResponseEntity<BaseResponse<List<DTO>>> getAll(Pageable pageable){
		BaseResponse<List<DTO>> response = BaseResponse.<List<DTO>>builder()
				.data((List<DTO>) baseServiceImpl.getAll(pageable))
				.code(HttpStatus.OK.value())
				.status(true)
				.build();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Get by ID", description = "Returns by ID")
	public ResponseEntity<BaseResponse<DTO>> getById(@PathVariable ID id){
		BaseResponse<DTO> response = BaseResponse.<DTO>builder()
				.data((DTO)baseServiceImpl.getById(id))
				.code(HttpStatus.OK.value())
				.status(true)
				.build();
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
	@Operation(summary = "Update by ID", description = "Returns the created object")
	public ResponseEntity<BaseResponse<DTO>> updateById(@PathVariable ID id,@RequestBody ObjectNode requestObj){
		BaseResponse<DTO> response = BaseResponse.<DTO>builder()
				.data((DTO)baseServiceImpl.updateById(id, requestObj))
				.code(HttpStatus.OK.value())
				.status(true)
				.build();
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	@Operation(summary = "Insert", description = "Returns the created object")
	public ResponseEntity<BaseResponse<DTO>> insert(@RequestBody DTO dto){
		BaseResponse<DTO> response = BaseResponse.<DTO>builder()
				.data((DTO)baseServiceImpl.save(dto))
				.code(HttpStatus.CREATED.value())
				.status(true)
				.build();
		return new ResponseEntity(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete by ID")
	public ResponseEntity<BaseResponse> deleteById(@PathVariable ID id){
		baseServiceImpl.deleteById(id);
		BaseResponse response = BaseResponse.builder()
				.code(HttpStatus.OK.value())
				.status(true)
				.build();
		
		return ResponseEntity.ok(response);
	}
	

}

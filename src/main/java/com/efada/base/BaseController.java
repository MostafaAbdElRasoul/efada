package com.efada.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseController<ID, DTO, S extends BaseServiceImpl>{
	
	@Autowired
	private S baseServiceImpl;
	
	@GetMapping
	public ResponseEntity<List<DTO>> getAll(){
		return new ResponseEntity(baseServiceImpl.getAll(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DTO> getById(@PathVariable ID id){
		return new ResponseEntity(baseServiceImpl.getById(id), HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<DTO> updateById(@PathVariable ID id,@RequestBody ObjectNode requestObj){
		return new ResponseEntity(baseServiceImpl.updateById(id, requestObj), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<DTO> insert(@RequestBody DTO dto){
		return new ResponseEntity(baseServiceImpl.save(dto), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable ID id){
		baseServiceImpl.deleteById(id);
		return new ResponseEntity(HttpStatus.OK);
	}
	

}

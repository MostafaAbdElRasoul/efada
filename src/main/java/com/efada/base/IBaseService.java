package com.efada.base;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IBaseService<E, ID, DTO> {

	public DTO getById(ID id);
	
	public DTO getOne(ID id);
	
	public List<DTO> getAll();

	public DTO save(DTO dto);

	public void deleteById(ID id);
	
	public DTO updateById(ID id, ObjectNode dto);
	
	public E getEntity();
	
	public DTO getDTO();
}

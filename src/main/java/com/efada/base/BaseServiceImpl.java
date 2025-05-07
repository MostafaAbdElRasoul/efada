package com.efada.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseServiceImpl<E, ID> {

	@Autowired
	private BaseRepository<E, ID> baseRepository;
	
	public List<E> getAll(){
		return baseRepository.findAll();
	}
	
	public E getOne(ID id) {
		return baseRepository.getReferenceById(id);
	}
	
	public E findById(ID id) {
		return baseRepository.findById(id).orElse(null);
	}
	
	public E insert(E entity) {
		return baseRepository.save(entity);

	}
	
	public E update(E entity) {
		return baseRepository.save(entity);
	}
	
	public void deleteById(ID id) {
		baseRepository.deleteById(id);
	}
	
	public void deleteAll(List<ID> ids) {
		baseRepository.deleteAllById(ids);
	}
}

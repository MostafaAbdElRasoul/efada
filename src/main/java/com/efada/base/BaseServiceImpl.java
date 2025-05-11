package com.efada.base;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.efada.exception.EfadaCustomException;
import com.efada.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.MappedSuperclass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@MappedSuperclass
public abstract class BaseServiceImpl<E, ID, DTO> implements IBaseService<E, ID, DTO>{

	@Autowired
	private BaseRepository<E, ID> baseRepository;

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public DTO getById(ID id) {
		return (DTO) ObjectMapperUtils.map(baseRepository.findById(id).get(), getDTO().getClass());
	}

	@Override
	public DTO getOne(ID id) {
		return (DTO) ObjectMapperUtils.map(baseRepository.getReferenceById(id), getDTO().getClass());
	}

	@Override
	public List<DTO> getAll(Pageable pageable) {
		String idPropertyName = getEntity().getClass().getDeclaredFields()[0].getName();
		
		Pageable sortedPageable = PageRequest.of(
		        pageable.getPageNumber(),
		        pageable.getPageSize(),
		        Sort.by(Sort.Direction.DESC, idPropertyName)
		    );

		    // Fetch entities with pagination and sorting
		    Page<E> entityPage = baseRepository.findAll(sortedPageable);

		    // Map entities to DTOs
		    return (List<DTO>) ObjectMapperUtils.mapAll(entityPage.getContent(), getDTO().getClass());
	}
	
	@Override
	public DTO save(DTO dto) {
		// TODO Auto-generated method stub
		E entity = (E) ObjectMapperUtils.map(dto, getEntity().getClass());
		 E entityAfterSaving = (E)baseRepository.save(entity);
		return (DTO) ObjectMapperUtils.map(entityAfterSaving, getDTO().getClass());
	}

	@Override
	public void deleteById(ID id) {
		baseRepository.deleteById(id);
	}

	@Override
	public DTO updateById(ID id, ObjectNode obj) {
		try {
			log.info("request object to update : "+ obj);
			E entity = (E) baseRepository.findById(id).get();
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = mapper.convertValue(obj, ObjectNode.class);
			Class entityClass = entity.getClass();
			
			
			Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				updateField(fieldNames.next(), entity, node, entityClass);
			}		
			baseRepository.save(entity);
			DTO dto = (DTO) ObjectMapperUtils.map(entity, getDTO().getClass());
		    return dto;
			
		}catch (NoSuchElementException ex) {
			throw new NoSuchElementException("NO_VALUE_PRESENT");
		} 
		catch (Exception ex) {
			throw new EfadaCustomException("UPDATE_OPERATION_FAILED");
		}
	}

	@Override
	public abstract E getEntity();

	@Override
	public abstract DTO getDTO();
	
	private void updateField(String fieldName, E entity, ObjectNode node, Class entityClass)
			throws SecurityException, IllegalArgumentException, IllegalAccessException {
		try {
			Field entityField = entityClass.getDeclaredField(fieldName);
			entityField.setAccessible(true);
			Object value = mapper.convertValue(node.get(fieldName), entityField.getType());
			entityField.set(entity, value);
			entityField.setAccessible(false);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}
	
}

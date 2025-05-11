package com.efada.base;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.efada.exception.EfadaCustomException;
import com.efada.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseServiceImpl<E, ID, DTO> implements IBaseService<E, ID, DTO>{

	@Autowired
	private BaseRepository<E, ID> baseRepository;

//	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public DTO getById(ID id) {
		return (DTO) ObjectMapperUtils.map(baseRepository.findById(id).orElseThrow(() -> new EfadaCustomException("ID_NOT_FOUND")), getDTO().getClass());
	}

	@Override
	public DTO getOne(ID id) {
		return (DTO) ObjectMapperUtils.map(baseRepository.getReferenceById(id), getDTO().getClass());
	}

	@Override
	public List<DTO> getAll() {
		String idPropertyName = getEntity().getClass().getDeclaredFields()[0].getName();
		List<E> entityList = (List<E>) baseRepository.findAllOrderByDesc(idPropertyName);
		return (List<DTO>) ObjectMapperUtils.mapAll(baseRepository.findAll(), getDTO().getClass());
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
			E entity = (E) baseRepository.findById(id).orElseThrow(() -> new EfadaCustomException("ID_NOT_FOUND"));
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = ObjectMapperUtils.map(obj, ObjectNode.class);
			Class entityClass = entity.getClass();
			
			
			Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				updateField(fieldNames.next(), entity, node, entityClass);
			}		
			baseRepository.save(entity);
			DTO dto = (DTO) ObjectMapperUtils.map(entity, getDTO().getClass());
		    return dto;
			
		} catch (Exception ex) {
			ex.printStackTrace();
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
			Object value = ObjectMapperUtils.map(node.get(fieldName), entityField.getType());
			entityField.set(entity, value);
			entityField.setAccessible(false);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}
	
}

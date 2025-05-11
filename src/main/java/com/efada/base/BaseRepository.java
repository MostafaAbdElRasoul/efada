package com.efada.base;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E, ID> extends JpaRepository<E, ID>{
	
	default List<E> findAllOrderByDesc(String property) {
        return findAll(Sort.by(Sort.Direction.DESC, property));
    }
	
}

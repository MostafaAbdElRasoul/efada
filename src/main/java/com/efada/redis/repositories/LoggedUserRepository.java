package com.efada.redis.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.efada.redis.entities.LoggedUser;

@Repository
public interface LoggedUserRepository extends CrudRepository<LoggedUser, String>{
	LoggedUser findByUserName(String userName);
}

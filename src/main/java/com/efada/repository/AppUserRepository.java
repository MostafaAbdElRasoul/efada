package com.efada.repository;

import org.springframework.stereotype.Repository;

import com.efada.base.BaseRepository;
import com.efada.entity.AppUser;
import java.util.List;
import java.util.Optional;


@Repository
public interface AppUserRepository extends BaseRepository<AppUser, Long>{

	Optional<AppUser> findByUsernameIgnoreCase(String username);
	
	boolean existsByEmailOrUsername(String email, String username);
}

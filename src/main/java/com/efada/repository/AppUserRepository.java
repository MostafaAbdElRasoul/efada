package com.efada.repository;

import org.springframework.stereotype.Repository;

import com.efada.base.BaseRepository;
import com.efada.entity.AppUser;

@Repository
public interface AppUserRepository extends BaseRepository<AppUser, Long>{

}

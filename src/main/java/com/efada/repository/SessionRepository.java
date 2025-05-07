package com.efada.repository;

import org.springframework.stereotype.Repository;

import com.efada.base.BaseRepository;
import com.efada.entity.Session;

@Repository
public interface SessionRepository extends BaseRepository<Session, Long>{

}

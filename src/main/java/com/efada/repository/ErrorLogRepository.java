package com.efada.repository;

import org.springframework.stereotype.Repository;

import com.efada.base.BaseRepository;
import com.efada.entity.ErrorLog;

@Repository
public interface ErrorLogRepository extends BaseRepository<ErrorLog, Long>{

}

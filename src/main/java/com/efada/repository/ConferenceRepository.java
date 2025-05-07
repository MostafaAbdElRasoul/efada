package com.efada.repository;

import org.springframework.stereotype.Repository;

import com.efada.base.BaseRepository;
import com.efada.entity.Conference;

@Repository
public interface ConferenceRepository extends BaseRepository<Conference, Long>{

}

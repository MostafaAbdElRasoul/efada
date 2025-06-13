package com.efada.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.efada.entity.UserLoginHistory;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long>{

}

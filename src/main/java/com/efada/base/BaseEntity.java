package com.efada.base;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Column(name="CREATED_BY" , updatable = false)
	@CreatedBy
	private Long createdBy;
	@Column(name="UPDATED_BY")
	
	@LastModifiedBy
	private Long updatedBy;
	
	@Column(name="CREATION_DATE" , updatable = false)
	@CreatedDate
	private Date creationDate;
	
	@Column(name="UPDATE_DATE")
	@LastModifiedDate 
	private Date updateDate;
	
	@Column(name="VERSION_NO")
	@Version
	private int versionNo;
}

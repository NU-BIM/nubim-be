package com.soyeon.nubim.common;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

	// TODO: 추후 주석 제거
	// @Column(nullable = false, updatable = false)
	// private String createdBy;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// TODO : 추후 주석 제거
	// @Column(nullable = false)
	// private String updatedBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@JsonIgnore
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = Boolean.FALSE;
}

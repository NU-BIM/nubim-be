package com.soyeon.nubim.common;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
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

}

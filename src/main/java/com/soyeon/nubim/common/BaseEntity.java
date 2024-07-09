package com.soyeon.nubim.common;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE #{entityName} SET is_deleted = true")
@SQLRestriction("is_deleted = false")
@Data
public abstract class BaseEntity {

	// @Column(nullable = false, updatable = false)
	// private String createdBy;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// @Column(nullable = false)
	// private String updatedBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@JsonIgnore
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = Boolean.FALSE;
}

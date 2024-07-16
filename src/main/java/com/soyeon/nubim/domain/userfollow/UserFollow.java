package com.soyeon.nubim.domain.userfollow;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.soyeon.nubim.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "user_follows",
	uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
@Data
public class UserFollow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userFollowId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id", nullable = false)
	private User follower;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followee_id", nullable = false)
	private User followee;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

}

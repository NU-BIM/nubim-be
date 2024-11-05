package com.soyeon.nubim.domain.user_block;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_block")
public class UserBlock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userBlockId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocking_user_id", nullable = false)
	private User blockingUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_user_id", nullable = false)
	private User blockedUser;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime blockedAt;
}

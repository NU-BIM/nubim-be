package com.soyeon.nubim.domain.report;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.soyeon.nubim.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id")
	private User reporter;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportType reportType;

	@Column(nullable = false)
	private Long targetId;

	@Column(length = 200)
	private String reportContent;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportStatus status = ReportStatus.PENDING;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public enum ReportType {
		POST, COMMENT
	}

	public enum ReportStatus {
		PENDING, REJECTED, COMPLETED
	}
}


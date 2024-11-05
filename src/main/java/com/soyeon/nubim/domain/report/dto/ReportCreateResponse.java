package com.soyeon.nubim.domain.report.dto;

import java.time.LocalDateTime;

import com.soyeon.nubim.domain.report.Report;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReportCreateResponse {
	private Long reportId;
	private Report.ReportType reportType;
	private Long targetId;
	private String reportContent;
	private LocalDateTime createdAt;
}

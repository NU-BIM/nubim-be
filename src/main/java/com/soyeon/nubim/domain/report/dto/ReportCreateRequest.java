package com.soyeon.nubim.domain.report.dto;

import com.soyeon.nubim.domain.report.Report;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReportCreateRequest {
	private Report.ReportType reportType;
	private Long targetId;
	private String reportContent;
}

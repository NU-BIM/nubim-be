package com.soyeon.nubim.domain.report;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.report.dto.ReportCreateRequest;
import com.soyeon.nubim.domain.report.dto.ReportCreateResponse;
import com.soyeon.nubim.domain.user.LoggedInUserService;
import com.soyeon.nubim.domain.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportMapper {
	private final LoggedInUserService loggedInUserService;

	public Report toReport(ReportCreateRequest request) {
		User reporter = loggedInUserService.getCurrentUser();

		return Report.builder()
			.reporter(reporter)
			.reportType(request.getReportType())
			.targetId(request.getTargetId())
			.reportContent(request.getReportContent())
			.status(Report.ReportStatus.PENDING)
			.build();
	}

	public ReportCreateResponse toReportCreateResponse(Report report) {
		return ReportCreateResponse.builder()
			.reportId(report.getReportId())
			.reportType(report.getReportType())
			.targetId(report.getTargetId())
			.reportContent(report.getReportContent())
			.createdAt(report.getCreatedAt())
			.build();
	}
}

package com.soyeon.nubim.domain.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.report.dto.ReportCreateRequest;
import com.soyeon.nubim.domain.report.dto.ReportCreateResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/reports")
public class ReportControllerV1 {
	private final ReportService reportService;

	@Operation(description = "댓글(COMMENT), 게시글(POST) id를 통해 신고")
	@PostMapping
	public ResponseEntity<ReportCreateResponse> createReport(@RequestBody ReportCreateRequest request) {
		return ResponseEntity.created(null)
			.body(reportService.createReport(request));
	}
}

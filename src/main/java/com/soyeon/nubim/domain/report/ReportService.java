package com.soyeon.nubim.domain.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soyeon.nubim.domain.comment.CommentRepository;
import com.soyeon.nubim.domain.comment.exceptions.CommentNotFoundException;
import com.soyeon.nubim.domain.post.PostRepository;
import com.soyeon.nubim.domain.post.exceptions.PostNotFoundException;
import com.soyeon.nubim.domain.report.dto.ReportCreateRequest;
import com.soyeon.nubim.domain.report.dto.ReportCreateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;
	private final ReportMapper reportMapper;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public ReportCreateResponse createReport(ReportCreateRequest requestDto) {
		Report report = reportMapper.toReport(requestDto);
		if (report.getReportType() == Report.ReportType.POST) {
			if (!postRepository.existsById(report.getTargetId())) {
				throw new PostNotFoundException(report.getTargetId());
			}
		} else if (report.getReportType() == Report.ReportType.COMMENT) {
			if (!commentRepository.existsById(report.getTargetId())) {
				throw new CommentNotFoundException(report.getReportId());
			}
		}
		reportRepository.save(report);

		return reportMapper.toReportCreateResponse(report);
	}
}

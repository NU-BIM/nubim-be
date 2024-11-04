package com.soyeon.nubim.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
		"FROM Report r " +
		"WHERE r.targetId = :postId AND r.reportType = 'POST' AND r.status = 'PENDING'")
	boolean isPostReportedAndPending(Long postId);
}

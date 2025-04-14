package com.PFE.DTT.repository;

import com.PFE.DTT.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByReportUsersUserId(Long userId);
}
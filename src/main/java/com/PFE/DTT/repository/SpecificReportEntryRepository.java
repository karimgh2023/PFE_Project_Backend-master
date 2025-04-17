package com.PFE.DTT.repository;

import com.PFE.DTT.model.SpecificReportEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificReportEntryRepository extends JpaRepository<SpecificReportEntry, Long> {
    
    @Query("SELECT s FROM SpecificReportEntry s WHERE s.report.id = :reportId")
    List<SpecificReportEntry> findByReportId(@Param("reportId") Long reportId);
}
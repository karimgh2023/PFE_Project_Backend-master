package com.PFE.DTT.repository;

import com.PFE.DTT.model.StandardReportEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardReportEntryRepository extends JpaRepository<StandardReportEntry, Long> {
    
    @Query("SELECT s FROM StandardReportEntry s WHERE s.report.id = :reportId")
    List<StandardReportEntry> findByReportId(@Param("reportId") Long reportId);
}
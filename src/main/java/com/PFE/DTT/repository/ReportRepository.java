package com.PFE.DTT.repository;

import com.PFE.DTT.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.createdBy.id = :userId ORDER BY r.createdAt DESC")
    List<Report> findByCreatedBy(@Param("userId") Long userId);

    @Query("SELECT r FROM Report r JOIN r.assignedUsers u WHERE u.id = :userId ORDER BY r.createdAt DESC")
    List<Report> findAssignedToUser(@Param("userId") Long userId);
}
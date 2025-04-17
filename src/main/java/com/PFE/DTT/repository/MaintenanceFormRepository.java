package com.PFE.DTT.repository;

import com.PFE.DTT.model.MaintenanceForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaintenanceFormRepository extends JpaRepository<MaintenanceForm, Long> {
    Optional<MaintenanceForm> findByReportId(Long reportId);
}
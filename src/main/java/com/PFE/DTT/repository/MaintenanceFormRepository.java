package com.PFE.DTT.repository;

import com.PFE.DTT.model.MaintenanceForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaintenanceFormRepository extends JpaRepository<MaintenanceForm, Integer> {
    Optional<MaintenanceForm> findByReportId(int reportId);
}
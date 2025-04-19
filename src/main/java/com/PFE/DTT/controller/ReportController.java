package com.PFE.DTT.controller;

import com.PFE.DTT.dto.*;
import com.PFE.DTT.model.*;
import com.PFE.DTT.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

@RestController
@RequestMapping("/api/rapports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StandardControlCriteriaRepository standardControlCriteriaRepository;

    @Autowired
    private SpecificControlCriteriaRepository specificControlCriteriaRepository;

    @Autowired
    private StandardReportEntryRepository standardReportEntryRepository;

    @Autowired
    private SpecificReportEntryRepository specificReportEntryRepository;

    @Autowired
    private MaintenanceFormRepository maintenanceFormRepository;

    /**
     * Get all reports 
     * @return List of reports
     */
    @GetMapping
    public ResponseEntity<?> getAllReports(@AuthenticationPrincipal User user) {
        if (user == null) {
            logger.error("Attempted to access reports without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse(false, "Authentication required to access reports")
            );
        }
        
        logger.info("User {} (role: {}) requesting all reports", user.getEmail(), user.getRole());
        
        try {
            List<Report> reports = reportRepository.findAll();
            List<ReportDTO> reportDTOs = reports.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            logger.info("Returning {} reports", reportDTOs.size());
            return ResponseEntity.ok(reportDTOs);
        } catch (Exception e) {
            logger.error("Error retrieving reports", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving reports: " + e.getMessage()));
        }
    }

    /**
     * Get report by ID
     * @param id Report ID
     * @return Report details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            logger.error("Attempted to access report {} without authentication", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse(false, "Authentication required to access report")
            );
        }
        
        logger.info("User {} requesting report with ID: {}", user.getEmail(), id);
        
        Optional<Report> reportOpt = reportRepository.findById(id);
        if (reportOpt.isEmpty()) {
            logger.warn("Report with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Report not found"));
        }
        
        Report report = reportOpt.get();
        
        // Check if user is authorized to view this report (creator or assigned)
        boolean isCreator = report.getCreatedBy().getId().equals(user.getId());
        boolean isAssigned = report.getAssignedUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        
        if (!isCreator && !isAssigned && !isAdmin) {
            logger.warn("User {} attempted to access report {} without permission", user.getEmail(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You are not authorized to view this report"));
        }
        
        ReportDTO reportDTO = mapToDTO(report);
        
        // Include additional details needed for the UI
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("report", reportDTO);
        
        // Add standard entries
        List<StandardReportEntry> standardEntries = standardReportEntryRepository.findByReportId(id);
        responseData.put("standardEntries", standardEntries);
        
        // Add specific entries
        List<SpecificReportEntry> specificEntries = specificReportEntryRepository.findByReportId(id);
        responseData.put("specificEntries", specificEntries);
        
        // Add maintenance form if exists
        Optional<MaintenanceForm> maintenanceForm = maintenanceFormRepository.findByReportId(id);
        maintenanceForm.ifPresent(form -> responseData.put("maintenanceForm", form));
        
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody ReportCreationRequest request,
                                          @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            logger.error("Attempted to create report without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authentication required to create reports"));
        }

        logger.info("User {} (role: {}) attempting to create a new report", currentUser.getEmail(), currentUser.getRole());

        if (currentUser.getRole() != User.Role.DEPARTMENT_MANAGER) {
            logger.warn("User {} with role {} attempted to create a report", currentUser.getEmail(), currentUser.getRole());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Only department managers can create reports"));
        }

        Optional<Protocol> optionalProtocol = protocolRepository.findById(request.getProtocolId());
        if (optionalProtocol.isEmpty()) {
            logger.warn("Invalid protocol ID: {}", request.getProtocolId());
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid protocol ID"));
        }
        Protocol protocol = optionalProtocol.get();

        // Collect required department IDs
        Set<Integer> requiredDepartmentIds = new HashSet<>();
        List<StandardControlCriteria> allStandardCriteria = standardControlCriteriaRepository.findAll();
        for (StandardControlCriteria sc : allStandardCriteria) {
            requiredDepartmentIds.add(sc.getImplementationResponsible().getId());
            requiredDepartmentIds.add(sc.getCheckResponsible().getId());
        }
        for (SpecificControlCriteria spc : protocol.getSpecificControlCriteria()) {
            spc.getImplementationResponsibles().forEach(d -> requiredDepartmentIds.add(d.getId()));
            spc.getCheckResponsibles().forEach(d -> requiredDepartmentIds.add(d.getId()));
        }

        // Validate user assignments
        Map<Integer, Long> departmentToUserMap = new HashMap<>();
        for (UserAssignmentDTO ua : request.getAssignedUsers()) {
            departmentToUserMap.put(ua.getDepartmentId(), ua.getUserId());
        }

        if (!departmentToUserMap.keySet().containsAll(requiredDepartmentIds)) { 
            logger.warn("Missing user assignments for required departments");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "A user must be assigned for each required department"));
        }

        try {
            // Create report and set fields
            Report report = new Report();
            report.setProtocol(protocol);
            report.setCreatedBy(currentUser);
            report.setCreatedAt(LocalDateTime.now());
            report.setIsCompleted(false);
            report.setType(request.getType());
            report.setSerialNumber(request.getSerialNumber());
            report.setEquipmentDescription(request.getEquipmentDescription());
            report.setDesignation(request.getDesignation());
            report.setManufacturer(request.getManufacturer());
            report.setImmobilization(request.getImmobilization());
            report.setServiceSeg(request.getServiceSeg());
            report.setBusinessUnit(request.getBusinessUnit());

            // Ensure the list is initialized
            // Use a regular loop instead of lambda to avoid 'effectively final' issue
            Set<User> assignedUsers = new HashSet<>();
            for (UserAssignmentDTO ua : request.getAssignedUsers()) {
                userRepository.findById(ua.getUserId()).ifPresent(assignedUsers::add);
            }
            report.setAssignedUsers(assignedUsers);

            // Save the report once with cascade
            Report savedReport = reportRepository.save(report);
            logger.info("Created new report with ID: {}", savedReport.getId());

            // Create standard report entries
            for (StandardControlCriteria sc : allStandardCriteria) {
                StandardReportEntry entry = new StandardReportEntry();
                entry.setReport(savedReport);
                entry.setStandardControlCriteria(sc);
                entry.setImplemented(false);
                entry.setAction("");
                entry.setResponsableAction("");
                entry.setDeadline("");
                entry.setSuccessControl("");
                entry.setUpdated(false);
                standardReportEntryRepository.save(entry);
            }

            // Create specific report entries
            for (SpecificControlCriteria spc : protocol.getSpecificControlCriteria()) {
                SpecificReportEntry entry = new SpecificReportEntry();
                entry.setReport(savedReport);
                entry.setSpecificControlCriteria(spc);
                entry.setHomologation(false);
                entry.setAction("");
                entry.setResponsableAction("");
                entry.setDeadline("");
                entry.setSuccessControl("");
                entry.setUpdated(false);
                specificReportEntryRepository.save(entry);
            }

            // Create maintenance form
            MaintenanceForm form = new MaintenanceForm();
            form.setReport(savedReport);
            form.setControlStandard(null);
            form.setCurrentType(null);
            form.setNetworkForm(null);
            form.setPowerCircuit("");
            form.setControlCircuit("");
            form.setFuseValue("");
            form.setHasTransformer(false);
            form.setFrequency("");
            form.setPhaseBalanceTest380v("");
            form.setPhaseBalanceTest210v("");
            form.setInsulationResistanceMotor("");
            form.setInsulationResistanceCable("");
            form.setMachineSizeHeight("");
            form.setMachineSizeLength("");
            form.setMachineSizeWidth("");
            form.setIsInOrder(false);
            maintenanceFormRepository.save(form);

            // Return success response with the report ID
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report created successfully");
            response.put("reportId", savedReport.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating report: " + e.getMessage()));
        }
    }

    @PutMapping("/entry/specific/{entryId}")
    public ResponseEntity<?> updateSpecificEntry(
            @PathVariable Long entryId,
            @RequestBody SpecificReportEntryUpdateRequest req,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            logger.error("Attempted to update specific entry without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authentication required"));
        }

        logger.info("User {} attempting to update specific entry {}", user.getEmail(), entryId);

        Optional<SpecificReportEntry> optionalEntry = specificReportEntryRepository.findById(entryId);
        if (optionalEntry.isEmpty()) {
            logger.warn("Specific entry with ID {} not found", entryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Entry not found"));
        }

        SpecificReportEntry entry = optionalEntry.get();

        if (entry.isUpdated()) {
            logger.warn("Attempted to update already updated specific entry {}", entryId);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "This entry has already been updated"));
        }

        SpecificControlCriteria criteria = entry.getSpecificControlCriteria();

        boolean isAssignedUser = entry.getReport().getAssignedUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        boolean isCheckDept = criteria.getCheckResponsibles().stream()
                .anyMatch(dep -> Objects.equals(dep.getId(), user.getDepartment().getId()));

        if (!isAssignedUser || !isCheckDept) {
            logger.warn("User {} not authorized to update specific entry {}", user.getEmail(), entryId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You are not authorized to fill this entry"));
        }

        if (req.getHomologation() == null) {
            logger.warn("Missing required field 'homologation' for specific entry update");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Homologation field is required"));
        }

        try {
            entry.setHomologation(req.getHomologation());
            if (req.getHomologation()) {
                entry.setAction(null);
                entry.setResponsableAction(null);
                entry.setDeadline(null);
                entry.setSuccessControl(null);
            } else {
                if (req.getAction() == null || req.getResponsableAction() == null ||
                        req.getDeadline() == null || req.getSuccessControl() == null) {
                    logger.warn("Missing required fields for non-homologated specific entry");
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "All fields are required when homologation is false"));
                }
                entry.setAction(req.getAction());
                entry.setResponsableAction(req.getResponsableAction());
                entry.setDeadline(req.getDeadline());
                entry.setSuccessControl(req.getSuccessControl());
            }

            entry.setUpdated(true);
            specificReportEntryRepository.save(entry);
            logger.info("Successfully updated specific entry {}", entryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Specific entry updated successfully");
            response.put("entry", entry);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating specific entry {}", entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating entry: " + e.getMessage()));
        }
    }
    
    @PutMapping("/entry/standard/{entryId}")
    public ResponseEntity<?> updateStandardEntry(
            @PathVariable Long entryId,
            @RequestBody StandardReportEntryUpdateRequest req,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            logger.error("Attempted to update standard entry without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authentication required"));
        }

        logger.info("User {} attempting to update standard entry {}", user.getEmail(), entryId);

        Optional<StandardReportEntry> optionalEntry = standardReportEntryRepository.findById(entryId);
        if (optionalEntry.isEmpty()) {
            logger.warn("Standard entry with ID {} not found", entryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Entry not found"));
        }

        StandardReportEntry entry = optionalEntry.get();

        if (entry.isUpdated()) {
            logger.warn("Attempted to update already updated standard entry {}", entryId);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "This entry has already been updated"));
        }

        StandardControlCriteria criteria = entry.getStandardControlCriteria();

        boolean isAssignedUser = entry.getReport().getAssignedUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        boolean isCheckDept = Objects.equals(criteria.getCheckResponsible().getId(), user.getDepartment().getId());

        if (!isAssignedUser || !isCheckDept) {
            logger.warn("User {} not authorized to update standard entry {}", user.getEmail(), entryId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You are not authorized to fill this entry"));
        }

        if (req.getImplemented() == null) {
            logger.warn("Missing required field 'implemented' for standard entry update");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Implemented field is required"));
        }

        try {
            entry.setImplemented(req.getImplemented());
            if (req.getImplemented()) {
                entry.setAction(null);
                entry.setResponsableAction(null);
                entry.setDeadline(null);
                entry.setSuccessControl(null);
            } else {
                if (req.getAction() == null || req.getResponsableAction() == null ||
                        req.getDeadline() == null || req.getSuccessControl() == null) {
                    logger.warn("Missing required fields for non-implemented standard entry");
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "All fields are required when implemented is false"));
                }
                entry.setAction(req.getAction());
                entry.setResponsableAction(req.getResponsableAction());
                entry.setDeadline(req.getDeadline());
                entry.setSuccessControl(req.getSuccessControl());
            }

            entry.setUpdated(true);
            standardReportEntryRepository.save(entry);
            logger.info("Successfully updated standard entry {}", entryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Standard entry updated successfully");
            response.put("entry", entry);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating standard entry {}", entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating entry: " + e.getMessage()));
        }
    }

    /**
     * Mark a report as completed
     * @param reportId Report ID
     * @return Updated report status
     */
    @PatchMapping("/{reportId}/complete")
    public ResponseEntity<?> markReportAsCompleted(
            @PathVariable Long reportId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authentication required"));
        }
        
        logger.info("User {} attempting to mark report {} as completed", user.getEmail(), reportId);
        
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            logger.warn("Report with ID {} not found", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Report not found"));
        }
        
        Report report = reportOpt.get();
        
        // Check if user is authorized (creator, assigned, or admin)
        boolean isCreator = report.getCreatedBy().getId().equals(user.getId());
        boolean isAssigned = report.getAssignedUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        
        if (!isCreator && !isAssigned && !isAdmin) {
            logger.warn("User {} not authorized to complete report {}", user.getEmail(), reportId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You are not authorized to complete this report"));
        }
        
        // Check if report is already completed
        if (report.getIsCompleted()) {
            logger.warn("Report {} is already marked as completed", reportId);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Report is already marked as completed"));
        }
        
        try {
            report.setIsCompleted(true);
            Report updatedReport = reportRepository.save(report);
            logger.info("Successfully marked report {} as completed", reportId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report marked as completed successfully");
            response.put("report", mapToDTO(updatedReport));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error marking report {} as completed", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error completing report: " + e.getMessage()));
        }
    }

    @PutMapping("/maintenance-form/{reportId}/fill")
    public ResponseEntity<?> fillMaintenanceForm(
            @PathVariable Long reportId,
            @RequestBody MaintenanceForm updatedForm,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            logger.error("Attempted to update maintenance form without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authentication required"));
        }

        logger.info("User {} attempting to update maintenance form for report {}", user.getEmail(), reportId);

        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            logger.warn("Report with ID {} not found", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Report not found"));
        }

        Report report = reportOpt.get();

        Optional<MaintenanceForm> formOpt = maintenanceFormRepository.findByReportId(reportId);
        if (formOpt.isEmpty()) {
            logger.warn("Maintenance form for report {} not found", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Maintenance form not found"));
        }

        MaintenanceForm form = formOpt.get();

        boolean isAssigned = report.getAssignedUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!isAssigned) {
            logger.warn("User {} not assigned to report {}", user.getEmail(), reportId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You are not assigned to this report"));
        }

        String departmentName = user.getDepartment().getName().trim().toLowerCase();
        logger.info("User department: {}", departmentName);

        try {
            if ("maintenance system".equals(departmentName)) {
                form.setControlStandard(updatedForm.getControlStandard());
                form.setCurrentType(updatedForm.getCurrentType());
                form.setNetworkForm(updatedForm.getNetworkForm());
                form.setPowerCircuit(updatedForm.getPowerCircuit());
                form.setControlCircuit(updatedForm.getControlCircuit());
                form.setFuseValue(updatedForm.getFuseValue());
                form.setHasTransformer(updatedForm.getHasTransformer());
                form.setFrequency(updatedForm.getFrequency());
                form.setPhaseBalanceTest380v(updatedForm.getPhaseBalanceTest380v());
                form.setPhaseBalanceTest210v(updatedForm.getPhaseBalanceTest210v());
                form.setInsulationResistanceMotor(updatedForm.getInsulationResistanceMotor());
                form.setInsulationResistanceCable(updatedForm.getInsulationResistanceCable());
                form.setMachineSizeHeight(updatedForm.getMachineSizeHeight());
                form.setMachineSizeLength(updatedForm.getMachineSizeLength());
                form.setMachineSizeWidth(updatedForm.getMachineSizeWidth());

                maintenanceFormRepository.save(form);
                logger.info("Successfully updated maintenance form details for report {}", reportId);
                return ResponseEntity.ok(new ApiResponse(true, "Form details filled except isInOrder"));
            }

            if ("she".equals(departmentName)) {
                form.setIsInOrder(updatedForm.getIsInOrder());
                maintenanceFormRepository.save(form);
                logger.info("Successfully updated isInOrder field for report {}", reportId);
                return ResponseEntity.ok(new ApiResponse(true, "isInOrder field updated"));
            }

            logger.warn("User with department {} not authorized to update maintenance form", departmentName);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You do not have permission to update the maintenance form"));
        } catch (Exception e) {
            logger.error("Error updating maintenance form for report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating maintenance form: " + e.getMessage()));
        }
    }

    @GetMapping("/my-created")
    public ResponseEntity<?> getReportsCreatedByMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            logger.error("Attempted to access my-created reports without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "User not authenticated"));
        }

        logger.info("User {} (role: {}) requesting reports created by them", user.getEmail(), user.getRole());

        if (user.getRole() != User.Role.DEPARTMENT_MANAGER) {
            logger.warn("User {} with role {} attempted to view created reports", user.getEmail(), user.getRole());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Unauthorized: Only department managers can view created reports"));
        }

        try {
            List<Report> reports = reportRepository.findByCreatedBy(user.getId());
            List<ReportDTO> reportDTOs = reports.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            logger.info("Returning {} reports created by user {}", reportDTOs.size(), user.getEmail());
            return ResponseEntity.ok(reportDTOs);
        } catch (Exception e) {
            logger.error("Error retrieving reports created by user {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving reports: " + e.getMessage()));
        }
    }

    @GetMapping("/assigned")
    public ResponseEntity<?> getReportsAssignedToMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            logger.error("Attempted to access assigned reports without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "User not authenticated"));
        }

        logger.info("User {} requesting reports assigned to them", user.getEmail());

        try {
            List<Report> reports = reportRepository.findAssignedToUser(user.getId());
            List<ReportDTO> reportDTOs = reports.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            logger.info("Returning {} reports assigned to user {}", reportDTOs.size(), user.getEmail());
            return ResponseEntity.ok(reportDTOs);
        } catch (Exception e) {
            logger.error("Error retrieving reports assigned to user {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving assigned reports: " + e.getMessage()));
        }
    }

    private ReportDTO mapToDTO(Report report) {
        Set<AssignedUserDTO> assignedUserDTOs = report.getAssignedUsers().stream()
                .map(user -> {
                    Department department = user.getDepartment();
                    Plant plant = user.getPlant();
                    return new AssignedUserDTO(
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getProfilePhoto(),
                            department,
                            plant
                    );
                })
                .collect(Collectors.toSet());

        return new ReportDTO(
                report.getId(),
                report.getType(),
                report.getSerialNumber(),
                report.getEquipmentDescription(),
                report.getDesignation(),
                report.getManufacturer(),
                report.getImmobilization(),
                report.getServiceSeg(),
                report.getBusinessUnit(),
                report.getCreatedAt(),
                report.getCreatedBy(),
                assignedUserDTOs
        );
    }
}
package com.PFE.DTT.controller;

import com.PFE.DTT.dto.ReportCreationRequest;
import com.PFE.DTT.dto.StandardReportEntryUpdateRequest;
import com.PFE.DTT.dto.SpecificReportEntryUpdateRequest;
import com.PFE.DTT.dto.UserAssignmentDTO;
import com.PFE.DTT.model.*;
import com.PFE.DTT.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/rapports")
public class ReportController {

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

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody ReportCreationRequest request, @AuthenticationPrincipal User currentUser){

        if (currentUser.getRole() != User.Role.DEPARTMENT_MANAGER) {
            return ResponseEntity.status(403).body("Only department managers can create reports.");
        }

        Optional<Protocol> optionalProtocol = protocolRepository.findById(request.getProtocolId());
        if (optionalProtocol.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid protocol ID.");
        }

        Protocol protocol = optionalProtocol.get();

        // STEP 1: Collect all unique required department IDs
        Set<Integer> requiredDepartmentIds = new HashSet<>();

        // All standard control criteria in DB
        List<StandardControlCriteria> allStandardCriteria = standardControlCriteriaRepository.findAll();
        for (StandardControlCriteria sc : allStandardCriteria) {
            requiredDepartmentIds.add(sc.getImplementationResponsible().getId());
            requiredDepartmentIds.add(sc.getCheckResponsible().getId());
        }

        // All specific control criteria in selected protocol
        Set<Integer> specificDepartmentIds = new HashSet<>();
        for (SpecificControlCriteria spc : protocol.getSpecificControlCriteria()) {
            for (Department d : spc.getImplementationResponsibles()) {
                specificDepartmentIds.add(d.getId());
            }
            for (Department d : spc.getCheckResponsibles()) {
                specificDepartmentIds.add(d.getId());
            }
        }

        requiredDepartmentIds.addAll(specificDepartmentIds);

        // STEP 2: Validate user assignment
        HashMap<Object, Object> departmentToUserMap = new HashMap<>();
        for (UserAssignmentDTO ua : request.getAssignedUsers()) {
            departmentToUserMap.put(ua.getDepartmentId(), ua.getUserId());
        }

        if (!departmentToUserMap.keySet().containsAll(requiredDepartmentIds)) {
            return ResponseEntity.badRequest().body("A user must be assigned for each required department.");
        }

        // STEP 3: Create and populate Report
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

        reportRepository.save(report);

        // STEP 4: Create empty standard report entries
        for (StandardControlCriteria sc : allStandardCriteria) {
            StandardReportEntry entry = new StandardReportEntry();
            entry.setReport(report);
            entry.setStandardControlCriteria(sc);
            entry.setImplemented(false); // ✅ Add this line to fix the error
            entry.setAction("");
            entry.setResponsableAction("");
            entry.setDeadline("");
            entry.setSuccessControl("");
            standardReportEntryRepository.save(entry);

        }

        // STEP 5: Create empty specific report entries
        for (SpecificControlCriteria spc : protocol.getSpecificControlCriteria()) {
            SpecificReportEntry entry = new SpecificReportEntry();
            entry.setReport(report);
            entry.setSpecificControlCriteria(spc);
            entry.setHomologation(false);
            entry.setAction("");
            entry.setResponsableAction("");
            entry.setDeadline("");
            entry.setSuccessControl("");
            specificReportEntryRepository.save(entry);
        }

        // STEP 6: Create empty maintenance form
        MaintenanceForm form = new MaintenanceForm();

        form.setReport(report);

        form.setControlStandard(null);
        form.setCurrentType(null);
        form.setNetworkForm(null);

        form.setPowerCircuit("");
        form.setControlCircuit("");
        form.setFuseValue("");

        form.setHasTransformer(false); // required
        form.setFrequency("");
        form.setPhaseBalanceTest380v("");
        form.setPhaseBalanceTest210v("");
        form.setInsulationResistanceMotor("");
        form.setInsulationResistanceCable("");

        form.setMachineSizeHeight("");
        form.setMachineSizeLength("");
        form.setMachineSizeWidth("");

        form.setIsInOrder(false); // required

        maintenanceFormRepository.save(form);


        return ResponseEntity.ok("Report created successfully.");
    }

    @PutMapping("/entry/standard/{entryId}")
    public ResponseEntity<?> updateStandardEntry(
            @PathVariable int entryId,
            @RequestBody StandardReportEntryUpdateRequest req,
            @AuthenticationPrincipal User user
    ) {
        Optional<StandardReportEntry> optionalEntry = standardReportEntryRepository.findById(entryId);
        if (optionalEntry.isEmpty()) {
            return ResponseEntity.badRequest().body("Entry not found");
        }

        StandardReportEntry entry = optionalEntry.get();
        StandardControlCriteria criteria = entry.getStandardControlCriteria();

        // Check user role and department
        boolean isAssignedUser = entry.getReport().getReportUsers().stream()
                .anyMatch(ru -> ru.getUser().getId().equals(user.getId()));
        boolean isCheckDept = criteria.getCheckResponsible().getId() == (user.getDepartment().getId());

        if (!isAssignedUser || !isCheckDept) {
            return ResponseEntity.status(403).body("You are not authorized to fill this entry");
        }

        // Validation
        if (req.getImplemented() == null) {
            return ResponseEntity.badRequest().body("Implemented field is required");
        }

        entry.setImplemented(req.getImplemented());
        if (req.getImplemented()) {
            entry.setAction(null);
            entry.setResponsableAction(null);
            entry.setDeadline(null);
            entry.setSuccessControl(null);
        } else {
            if (req.getAction() == null || req.getResponsableAction() == null ||
                    req.getDeadline() == null || req.getSuccessControl() == null) {
                return ResponseEntity.badRequest().body("All fields are required when implemented is false");
            }
            entry.setAction(req.getAction());
            entry.setResponsableAction(req.getResponsableAction());
            entry.setDeadline(req.getDeadline());
            entry.setSuccessControl(req.getSuccessControl());
        }

        standardReportEntryRepository.save(entry);
        return ResponseEntity.ok("Standard entry updated successfully");
    }

    @PutMapping("/entry/specific/{entryId}")
    public ResponseEntity<?> updateSpecificEntry(
            @PathVariable int entryId,
            @RequestBody SpecificReportEntryUpdateRequest req,
            @AuthenticationPrincipal User user
    ) {
        Optional<SpecificReportEntry> optionalEntry = specificReportEntryRepository.findById(entryId);
        if (optionalEntry.isEmpty()) {
            return ResponseEntity.badRequest().body("Entry not found");
        }

        SpecificReportEntry entry = optionalEntry.get();
        SpecificControlCriteria criteria = entry.getSpecificControlCriteria();

        // Check if user is assigned to report
        boolean isAssignedUser = entry.getReport().getReportUsers().stream()
                .anyMatch(ru -> ru.getUser().getId().equals(user.getId()));

        // Check if user is from one of the check responsible departments
        boolean isCheckDept = criteria.getCheckResponsibles().stream()
                .anyMatch(dep -> dep.getId() == (user.getDepartment().getId()));

        if (!isAssignedUser || !isCheckDept) {
            return ResponseEntity.status(403).body("You are not authorized to fill this entry");
        }

        // Validation
        if (req.getHomologation() == null) {
            return ResponseEntity.badRequest().body("Homologation field is required");
        }

        entry.setHomologation(req.getHomologation());
        if (req.getHomologation()) {
            entry.setAction(null);
            entry.setResponsableAction(null);
            entry.setDeadline(null);
            entry.setSuccessControl(null);
        } else {
            if (req.getAction() == null || req.getResponsableAction() == null ||
                    req.getDeadline() == null || req.getSuccessControl() == null) {
                return ResponseEntity.badRequest().body("All fields are required when homologation is false");
            }
            entry.setAction(req.getAction());
            entry.setResponsableAction(req.getResponsableAction());
            entry.setDeadline(req.getDeadline());
            entry.setSuccessControl(req.getSuccessControl());
        }

        specificReportEntryRepository.save(entry);
        return ResponseEntity.ok("Specific entry updated successfully");
    }

    @PutMapping("/maintenance-form/{reportId}/fill")
    public ResponseEntity<?> fillMaintenanceForm(@PathVariable int reportId, @RequestBody MaintenanceForm updatedForm, @AuthenticationPrincipal User user) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) return ResponseEntity.badRequest().body("Report not found");

        Report report = reportOpt.get();

        Optional<MaintenanceForm> formOpt = maintenanceFormRepository.findByReportId(reportId);
        if (formOpt.isEmpty()) return ResponseEntity.badRequest().body("Maintenance form not found");

        MaintenanceForm form = formOpt.get();

        boolean isAssigned = report.getReportUsers().stream()
                .anyMatch(ru -> ru.getUser().getId().equals(user.getId()));

        if (!isAssigned) return ResponseEntity.status(403).body("You are not assigned to this report");

        if ("Maintenance system".equalsIgnoreCase(user.getDepartment().getName())) {
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
            return ResponseEntity.ok("Form details filled except isInOrder");
        }

        if ("SHE".equalsIgnoreCase(user.getDepartment().getName())) {
            form.setIsInOrder(updatedForm.getIsInOrder());
            maintenanceFormRepository.save(form);
            return ResponseEntity.ok("isInOrder field updated");
        }

        return ResponseEntity.status(403).body("You do not have permission to update the maintenance form");
    }
}
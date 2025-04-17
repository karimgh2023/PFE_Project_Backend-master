package com.PFE.DTT.controller;

import com.PFE.DTT.dto.ProtocolDTO;
import com.PFE.DTT.model.*;
import com.PFE.DTT.repository.DepartmentRepository;
import com.PFE.DTT.repository.ProtocolRepository;
import com.PFE.DTT.repository.SpecificControlCriteriaRepository;
import com.PFE.DTT.repository.UserRepository;
import com.PFE.DTT.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/protocols")
public class ProtocolController {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SpecificControlCriteriaRepository specificControlCriteriaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Get all protocols - available to all users
    @GetMapping
    public List<ProtocolDTO> getAllProtocols() {
        // Convert entities to DTOs to prevent infinite recursion
        return protocolRepository.findAll().stream()
                .map(ProtocolDTO::new)
                .collect(Collectors.toList());
    }

    // Get all protocol types
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllProtocolTypes() {
        // Extract all protocol types from enum
        List<String> protocolTypes = Arrays.stream(ProtocolType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(protocolTypes);
    }

    // ✅ Create a new Protocol (For authenticated users)
    @PostMapping("/create")
    public ResponseEntity<?> createProtocol(
            @RequestBody ProtocolRequest requestBody,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized: User not authenticated.");
        }

        ProtocolType protocolType;
        try {
            protocolType = ProtocolType.valueOf(requestBody.getProtocolType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid protocol type. Allowed values: HOMOLOGATION, REQUALIFICATION.");
        }

        // Create and save the protocol
        Protocol protocol = new Protocol(requestBody.getName(), protocolType, user);
        Protocol savedProtocol = protocolRepository.save(protocol);

        // Process specific control criteria if provided
        if (requestBody.getCriteria() != null && !requestBody.getCriteria().isEmpty()) {
            for (CriteriaRequest criteriaRequest : requestBody.getCriteria()) {
                // Validate and fetch departments
                Set<Department> implementationDepartments = new HashSet<>(
                        departmentRepository.findAllById(criteriaRequest.getImplementationDepartmentIds()));
                
                Set<Department> checkDepartments = new HashSet<>(
                        departmentRepository.findAllById(criteriaRequest.getCheckDepartmentIds()));
                
                if (implementationDepartments.isEmpty() || checkDepartments.isEmpty()) {
                    continue; // Skip invalid criteria
                }
                
                // Create specific control criteria
                SpecificControlCriteria criteria = new SpecificControlCriteria(
                        criteriaRequest.getDescription(),
                        implementationDepartments,
                        checkDepartments,
                        savedProtocol
                );
                specificControlCriteriaRepository.save(criteria);
            }
        }

        return ResponseEntity.ok(Map.of(
            "message", "Protocol created successfully.",
            "protocolId", savedProtocol.getId()
        ));
    }

    static class ProtocolRequest {
        private String name;
        private String protocolType;
        private List<CriteriaRequest> criteria;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getProtocolType() { return protocolType; }
        public void setProtocolType(String protocolType) { this.protocolType = protocolType; }
        
        public List<CriteriaRequest> getCriteria() { return criteria; }
        public void setCriteria(List<CriteriaRequest> criteria) { this.criteria = criteria; }
    }
    
    static class CriteriaRequest {
        private String description;
        private List<Integer> implementationDepartmentIds;
        private List<Integer> checkDepartmentIds;
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<Integer> getImplementationDepartmentIds() { return implementationDepartmentIds; }
        public void setImplementationDepartmentIds(List<Integer> implementationDepartmentIds) { 
            this.implementationDepartmentIds = implementationDepartmentIds; 
        }
        
        public List<Integer> getCheckDepartmentIds() { return checkDepartmentIds; }
        public void setCheckDepartmentIds(List<Integer> checkDepartmentIds) { 
            this.checkDepartmentIds = checkDepartmentIds; 
        }
    }
}

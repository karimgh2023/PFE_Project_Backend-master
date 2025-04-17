package com.PFE.DTT.controller;

import com.PFE.DTT.model.*;
import com.PFE.DTT.repository.SpecificControlCriteriaRepository;
import com.PFE.DTT.repository.DepartmentRepository;
import com.PFE.DTT.repository.ProtocolRepository;
import com.PFE.DTT.repository.UserRepository;
import com.PFE.DTT.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/specific-control-criteria")
public class SpecificControlCriteriaController {

    @Autowired
    private SpecificControlCriteriaRepository controlCriteriaRepository;

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createControlCriteria(
            @RequestBody ControlCriteriaRequest requestBody,
            HttpServletRequest request) {

        // ✅ Extract user ID from JWT token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: Missing or invalid token.");
        }
        int userId = jwtUtil.extractUserId(token.substring(7));

        Optional<User> user = userRepository.findById((long) userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(403).body("User not found.");
        }

        Optional<Protocol> protocol = protocolRepository.findById(requestBody.getProtocolId());
        if (protocol.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid protocol ID.");
        }

        if (!protocol.get().getCreatedBy().getId().equals(user.get().getId())) {
            return ResponseEntity.status(403).body("You are not authorized to add control criteria to this protocol.");
        }

        // ✅ Fetch multiple departments for implementation & check responsibilities
        Set<Department> implementationDepartments = new HashSet<>(departmentRepository.findAllById(requestBody.getImplementationResponsibleIds()));
        Set<Department> checkDepartments = new HashSet<>(departmentRepository.findAllById(requestBody.getCheckResponsibleIds()));

        if (implementationDepartments.isEmpty() || checkDepartments.isEmpty()) {
            return ResponseEntity.badRequest().body("One or more department IDs are invalid.");
        }

        SpecificControlCriteria controlCriteria = new SpecificControlCriteria(
                requestBody.getDescription(),
                implementationDepartments,
                checkDepartments,
                protocol.get()
        );
        controlCriteriaRepository.save(controlCriteria);

        return ResponseEntity.ok("ControlCriteria created successfully.");
    }

    // ✅ Updated DTO to support multiple department IDs
    static class ControlCriteriaRequest {
        private String description;
        private List<Integer> implementationResponsibleIds;
        private List<Integer> checkResponsibleIds;
        private int protocolId;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<Integer> getImplementationResponsibleIds() { return implementationResponsibleIds; }
        public void setImplementationResponsibleIds(List<Integer> implementationResponsibleIds) { this.implementationResponsibleIds = implementationResponsibleIds; }

        public List<Integer> getCheckResponsibleIds() { return checkResponsibleIds; }
        public void setCheckResponsibleIds(List<Integer> checkResponsibleIds) { this.checkResponsibleIds = checkResponsibleIds; }

        public int getProtocolId() { return protocolId; }
        public void setProtocolId(int protocolId) { this.protocolId = protocolId; }
    }
}

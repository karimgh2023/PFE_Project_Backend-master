package com.PFE.DTT.dto;

import com.PFE.DTT.model.User;
import java.time.LocalDateTime;
import java.util.Set;

public class ReportDTO {
    private Long id;
    private String type;
    private String serialNumber;
    private String equipmentDescription;
    private String designation;
    private String manufacturer;
    private String immobilization;
    private String serviceSeg;
    private String businessUnit;
    private LocalDateTime createdAt;
    private User createdBy;
    private Set<AssignedUserDTO> assignedUsers;

    public ReportDTO() {
    }

    public ReportDTO(Long id, String type, String serialNumber, String equipmentDescription,
                     String designation, String manufacturer, String immobilization,
                     String serviceSeg, String businessUnit, LocalDateTime createdAt,
                     User createdBy, Set<AssignedUserDTO> assignedUsers) {
        this.id = id;
        this.type = type;
        this.serialNumber = serialNumber;
        this.equipmentDescription = equipmentDescription;
        this.designation = designation;
        this.manufacturer = manufacturer;
        this.immobilization = immobilization;
        this.serviceSeg = serviceSeg;
        this.businessUnit = businessUnit;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.assignedUsers = assignedUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getEquipmentDescription() {
        return equipmentDescription;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        this.equipmentDescription = equipmentDescription;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getImmobilization() {
        return immobilization;
    }

    public void setImmobilization(String immobilization) {
        this.immobilization = immobilization;
    }

    public String getServiceSeg() {
        return serviceSeg;
    }

    public void setServiceSeg(String serviceSeg) {
        this.serviceSeg = serviceSeg;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<AssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<AssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
} 
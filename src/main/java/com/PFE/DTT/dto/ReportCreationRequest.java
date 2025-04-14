package com.PFE.DTT.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReportCreationRequest {
    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private int protocolId;
    private String type;
    private String serialNumber;
    private String equipmentDescription;
    private String designation;
    private String manufacturer;
    private String immobilization;
    private String serviceSeg;
    private String businessUnit;
    private List<UserAssignmentDTO> assignedUsers;

    // Getters & Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
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

    public List<UserAssignmentDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<UserAssignmentDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}

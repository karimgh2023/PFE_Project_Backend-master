package com.PFE.DTT.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "report") // Recommended for clarity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "protocol_id", nullable = false)
    private Protocol protocol; // Protocole déjà créé

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportUser> reportUsers = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardReportEntry> reportEntries; // Liste des entrées (critères)

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private MaintenanceForm maintenanceForm;

    private boolean isCompleted; // Indique si le rapport est terminé

    // Nouveaux champs ajoutés
    private String type;
    private String serialNumber;
    private String equipmentDescription;
    private String designation;
    private String manufacturer;
    private String immobilization;
    private String serviceSeg;
    private String businessUnit;

    // Default Constructor (Required for JPA)
    public Report() {}

    // Constructor with all fields (except ID, auto-generated)
    public Report(Protocol protocol, User createdBy, List<ReportUser> reportUsers, List<StandardReportEntry> reportEntries,
                  MaintenanceForm maintenanceForm, boolean isCompleted, String type, String serialNumber,
                  String equipmentDescription, String designation, String manufacturer, String immobilization,
                  String serviceSeg, String businessUnit) {
        this.protocol = protocol;
        this.createdBy = createdBy;
        this.reportUsers = reportUsers;
        this.reportEntries = reportEntries;
        this.createdAt = LocalDateTime.now();
        this.maintenanceForm = maintenanceForm;
        this.isCompleted = isCompleted;
        this.type = type;
        this.serialNumber = serialNumber;
        this.equipmentDescription = equipmentDescription;
        this.designation = designation;
        this.manufacturer = manufacturer;
        this.immobilization = immobilization;
        this.serviceSeg = serviceSeg;
        this.businessUnit = businessUnit;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<ReportUser> getReportUsers() {
        return reportUsers;
    }

    public void setReportUsers(List<ReportUser> reportUsers) {
        this.reportUsers = reportUsers;
    }

    public List<StandardReportEntry> getReportEntries() {
        return reportEntries;
    }

    public void setReportEntries(List<StandardReportEntry> reportEntries) {
        this.reportEntries = reportEntries;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MaintenanceForm getMaintenanceForm() {
        return maintenanceForm;
    }

    public void setMaintenanceForm(MaintenanceForm maintenanceForm) {
        this.maintenanceForm = maintenanceForm;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
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



    // toString() method for debugging
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", protocol=" + (protocol != null ? protocol.getId() : "N/A") +
                ", reportEntries=" + (reportEntries != null ? reportEntries.size() : 0) +
                ", createdAt=" + createdAt +
                ", isCompleted=" + isCompleted +
                '}';
    }

    // Equals & HashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

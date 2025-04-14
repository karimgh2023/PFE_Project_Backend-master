package com.PFE.DTT.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Represents an entry in a report, detailing specific actions, responsibilities, and outcomes
 * associated with either a specific or standard control criteria.
 */
@Entity
@Table
public class SpecificReportEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Boolean homologation;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String responsableAction;

    @Column(nullable = false)
    private String deadline;

    @Column(nullable = false)
    private String successControl;

    // Relationship with Report (Composition)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    // Relationship with SpecificControlCriteria (null if StandardControlCriteria is set)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specific_criteria_id")
    private SpecificControlCriteria specificControlCriteria;

    // Default Constructor (JPA Requirement)
    public SpecificReportEntry() {}

    // Constructor
    public SpecificReportEntry(boolean homologation, String action, String responsableAction, String deadline, String successControl, Report report) {
        this.homologation = homologation;
        this.action = action;
        this.responsableAction = responsableAction;
        this.deadline = deadline;
        this.successControl = successControl;
        this.report = report;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getHomologation() {
        return homologation;
    }

    public void setHomologation(Boolean homologation) {
        this.homologation = homologation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResponsableAction() {
        return responsableAction;
    }

    public void setResponsableAction(String responsableAction) {
        this.responsableAction = responsableAction;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getSuccessControl() {
        return successControl;
    }

    public void setSuccessControl(String successControl) {
        this.successControl = successControl;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public SpecificControlCriteria getSpecificControlCriteria() {
        return specificControlCriteria;
    }

    public void setSpecificControlCriteria(SpecificControlCriteria criteria) {
        this.specificControlCriteria = criteria;
    }


    @Override
    public String toString() {
        return "SpecificReportEntry{" +
                "id=" + id +
                ", homologation='" + homologation + '\'' +
                ", action='" + action + '\'' +
                ", responsableAction='" + responsableAction + '\'' +
                ", deadline='" + deadline + '\'' +
                ", successControl='" + successControl + '\'' +
                ", report=" + (report != null ? report.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecificReportEntry that)) return false;
        return id == that.id &&
                Objects.equals(homologation, that.homologation) &&
                Objects.equals(action, that.action) &&
                Objects.equals(responsableAction, that.responsableAction) &&
                Objects.equals(deadline, that.deadline) &&
                Objects.equals(successControl, that.successControl) &&
                Objects.equals(report, that.report) &&
                Objects.equals(specificControlCriteria, that.specificControlCriteria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, homologation, action, responsableAction, deadline, successControl, report, specificControlCriteria);
    }
}

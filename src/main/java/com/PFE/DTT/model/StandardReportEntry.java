package com.PFE.DTT.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Represents an entry in a report, detailing specific actions, responsibilities, and outcomes
 * associated with either a specific or standard control criteria.
 */
@Entity
@Table
public class StandardReportEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Boolean implemented;

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


    // Relationship with StandardControlCriteria (null if SpecificControlCriteria is set)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_criteria_id")
    private StandardControlCriteria standardControlCriteria;

    // Default Constructor (JPA Requirement)
    public StandardReportEntry() {}

    // Constructor
    public StandardReportEntry(Boolean implemented, String action, String responsableAction, String deadline, String successControl, Report report) {
        this.implemented = implemented;
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

    public Boolean getImplemented() {
        return implemented;
    }

    public void setImplemented(Boolean implemented) {
        this.implemented = implemented;
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

    public void setStandardControlCriteria(StandardControlCriteria criteria) {
        this.standardControlCriteria = criteria;
    }





    public StandardControlCriteria getStandardControlCriteria() {
        return standardControlCriteria;
    }


    @Override
    public String toString() {
        return "ReportEntry{" +
                "id=" + id +
                ", implemented=" + implemented +
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
        if (!(o instanceof StandardReportEntry that)) return false;
        return id == that.id &&
                Objects.equals(implemented, that.implemented) &&
                Objects.equals(action, that.action) &&
                Objects.equals(responsableAction, that.responsableAction) &&
                Objects.equals(deadline, that.deadline) &&
                Objects.equals(successControl, that.successControl) &&
                Objects.equals(report, that.report) &&
                Objects.equals(standardControlCriteria, that.standardControlCriteria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, implemented, action, responsableAction, deadline, successControl, report, standardControlCriteria);
    }
}
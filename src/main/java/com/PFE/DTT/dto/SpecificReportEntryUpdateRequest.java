package com.PFE.DTT.dto;

public class SpecificReportEntryUpdateRequest {
    private Boolean homologation;
    private String action;
    private String responsableAction;
    private String deadline;
    private String successControl;

    // Getters and setters
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
} 
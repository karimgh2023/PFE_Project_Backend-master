package com.PFE.DTT.dto;

import com.PFE.DTT.model.SpecificReportEntry;


public class SpecificReportEntryDTO {
    private int id;
    private boolean homologation;
    private String action;
    private String responsableAction;
    private String deadline;
    private String successControl;
    private boolean isUpdated;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isHomologation() { return homologation; }
    public void setHomologation(boolean homologation) { this.homologation = homologation; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResponsableAction() { return responsableAction; }
    public void setResponsableAction(String responsableAction) { this.responsableAction = responsableAction; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getSuccessControl() { return successControl; }
    public void setSuccessControl(String successControl) { this.successControl = successControl; }

    public boolean isUpdated() { return isUpdated; }
    public void setUpdated(boolean updated) { isUpdated = updated; }
} 
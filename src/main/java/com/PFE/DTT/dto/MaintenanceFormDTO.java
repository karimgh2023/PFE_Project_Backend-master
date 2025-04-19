package com.PFE.DTT.dto;

import com.PFE.DTT.model.MaintenanceForm;

public class MaintenanceFormDTO {
    private MaintenanceForm form;
    private boolean canEditMaintenance;
    private boolean canEditShe;

    public MaintenanceForm getForm() {
        return form;
    }

    public void setForm(MaintenanceForm form) {
        this.form = form;
    }

    public boolean isCanEditMaintenance() {
        return canEditMaintenance;
    }

    public void setCanEditMaintenance(boolean canEditMaintenance) {
        this.canEditMaintenance = canEditMaintenance;
    }

    public boolean isCanEditShe() {
        return canEditShe;
    }

    public void setCanEditShe(boolean canEditShe) {
        this.canEditShe = canEditShe;
    }
} 
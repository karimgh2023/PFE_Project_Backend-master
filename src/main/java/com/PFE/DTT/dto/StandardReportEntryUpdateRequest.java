package com.PFE.DTT.dto;

import lombok.Data;

@Data
public class StandardReportEntryUpdateRequest {
    private Boolean implemented;
    private String action;
    private String responsableAction;
    private String deadline;
    private String successControl;
} 
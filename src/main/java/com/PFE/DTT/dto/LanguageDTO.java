package com.PFE.DTT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDTO {
    private Long id;
    private String code;
    private String name;
    private String flagUrl;
    private boolean isDefault;
} 
package com.PFE.DTT.dto;

public class DepartmentRequest {
    private String name;

    // Default constructor (required for JSON deserialization)
    public DepartmentRequest() {
    }

    public DepartmentRequest(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
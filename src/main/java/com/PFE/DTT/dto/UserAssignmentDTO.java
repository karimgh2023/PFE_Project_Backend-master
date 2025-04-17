package com.PFE.DTT.dto;

public class UserAssignmentDTO {
    private int departmentId;
    private int userId;

    // Constructors
    public UserAssignmentDTO() {}

    public UserAssignmentDTO(int departmentId, int userId) {
        this.departmentId = departmentId;
        this.userId = userId;
    }

    // Getters and setters
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

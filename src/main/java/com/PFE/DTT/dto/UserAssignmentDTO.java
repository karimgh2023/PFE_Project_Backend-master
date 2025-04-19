package com.PFE.DTT.dto;

public class UserAssignmentDTO {
    private long userId;
    private int departmentId;

    // Constructors
    public UserAssignmentDTO() {}

    public UserAssignmentDTO(long userId, int departmentId) {
        this.userId = userId;
        this.departmentId = departmentId;
    }

    // Getters and setters
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
}

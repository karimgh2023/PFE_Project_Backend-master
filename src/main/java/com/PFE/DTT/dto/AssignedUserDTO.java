package com.PFE.DTT.dto;

import com.PFE.DTT.model.Department;
import com.PFE.DTT.model.Plant;

public class AssignedUserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePhoto;
    private Department department;
    private Plant plant;

    public AssignedUserDTO() {
    }

    public AssignedUserDTO(long id, String firstName, String lastName, String email, String profilePhoto, Department department, Plant plant) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.department = department;
        this.plant = plant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
} 
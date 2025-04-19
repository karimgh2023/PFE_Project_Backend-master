package com.PFE.DTT.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.EMPLOYEE; // Default role

    @Column(nullable = false)
    private boolean isVerified;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "profile_photo")
    private String profilePhoto = "https://res.cloudinary.com/dbgo6jzqe/image/upload/v1740737558/default_profile_idqbuv.png";

    // New fields
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // New relationship with Plant
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @JsonIgnore
    @ManyToMany(mappedBy = "assignedUsers")
    private Set<Report> reportsAssigned;

    public Set<Report> getReportsAssigned() {
        return reportsAssigned;
    }

    public void setReportsAssigned(Set<Report> reportsAssigned) {
        this.reportsAssigned = reportsAssigned;
    }


    // Define Role Enum
    public enum Role {
        ADMIN,
        DEPARTMENT_MANAGER,
        EMPLOYEE

    }

    // Default Constructor (JPA Requirement)
    public User() {}

    // Constructor
    public User(String email, String password, String phoneNumber, String firstName, String lastName, Department department, Plant plant) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.plant = plant;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", isVerified=" + isVerified +
                ", verificationCode='" + verificationCode + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department=" + (department != null ? department.getId() : "null") +
                ", plant=" + (plant != null ? plant.getId() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                role == that.role &&
                isVerified == that.isVerified &&
                Objects.equals(verificationCode, that.verificationCode) &&
                Objects.equals(profilePhoto, that.profilePhoto) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(department, that.department) &&
                Objects.equals(plant, that.plant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, role, isVerified, verificationCode, profilePhoto, phoneNumber, firstName, lastName, department, plant);
    }
}
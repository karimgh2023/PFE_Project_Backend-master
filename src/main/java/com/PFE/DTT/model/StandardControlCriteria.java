package com.PFE.DTT.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entity class representing standard control criteria in the system.
 * Maps to the standard_control_criteria table with relationships to Department entities.
 */
@Entity
@Table(name = "standard_control_criteria")
public class StandardControlCriteria {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implementation_responsible", nullable = false)
    private Department implementationResponsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_responsible", nullable = false)
    private Department checkResponsible;

    // Default Constructor
    public StandardControlCriteria() {}

    // Constructor with all fields
    public StandardControlCriteria(int id, String description, Department implementationResponsible, Department checkResponsible) {
        this.id = id;
        this.description = description;
        this.implementationResponsible = implementationResponsible;
        this.checkResponsible = checkResponsible;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Department getImplementationResponsible() {
        return implementationResponsible;
    }

    public void setImplementationResponsible(Department implementationResponsible) {
        this.implementationResponsible = implementationResponsible;
    }

    public Department getCheckResponsible() {
        return checkResponsible;
    }

    public void setCheckResponsible(Department checkResponsible) {
        this.checkResponsible = checkResponsible;
    }

    // toString
    @Override
    public String toString() {
        return "StandardControlCriteria{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", implementationResponsible=" + (implementationResponsible != null ? implementationResponsible.getId() : "null") +
                ", checkResponsible=" + (checkResponsible != null ? checkResponsible.getId() : "null") +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardControlCriteria that)) return false;
        return id == that.id &&
                Objects.equals(description, that.description) &&
                Objects.equals(implementationResponsible, that.implementationResponsible) &&
                Objects.equals(checkResponsible, that.checkResponsible);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, description, implementationResponsible, checkResponsible);
    }
}
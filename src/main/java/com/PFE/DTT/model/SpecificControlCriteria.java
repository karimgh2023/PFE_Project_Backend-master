package com.PFE.DTT.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity class representing specific control criteria in the system.
 * Defines attributes including departments responsible for implementation and checking,
 * and its association with a protocol.
 */
@Entity
@Table(name = "specific_control_criteria")
public class SpecificControlCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "specific_implementation_responsible",
            joinColumns = @JoinColumn(name = "criteria_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> implementationResponsibles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "specific_check_responsible",
            joinColumns = @JoinColumn(name = "criteria_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> checkResponsibles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "protocol_id", nullable = false)
    @JsonBackReference
    private Protocol protocol;

    // Default Constructor
    public SpecificControlCriteria() {}

    // Constructor with all fields
    public SpecificControlCriteria(String description, Set<Department> implementationResponsibles,
                                   Set<Department> checkResponsibles, Protocol protocol) {
        this.description = description;
        this.implementationResponsibles = implementationResponsibles != null ? implementationResponsibles : new HashSet<>();
        this.checkResponsibles = checkResponsibles != null ? checkResponsibles : new HashSet<>();
        this.protocol = protocol;
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

    public Set<Department> getImplementationResponsibles() {
        return implementationResponsibles;
    }

    public void setImplementationResponsibles(Set<Department> implementationResponsibles) {
        this.implementationResponsibles = implementationResponsibles != null ? implementationResponsibles : new HashSet<>();
    }

    public Set<Department> getCheckResponsibles() {
        return checkResponsibles;
    }

    public void setCheckResponsibles(Set<Department> checkResponsibles) {
        this.checkResponsibles = checkResponsibles != null ? checkResponsibles : new HashSet<>();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    // toString
    @Override
    public String toString() {
        return "SpecificControlCriteria{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", implementationResponsibles=" + implementationResponsibles +
                ", checkResponsibles=" + checkResponsibles +
                ", protocol=" + (protocol != null ? protocol.getName() : "null") +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecificControlCriteria that)) return false;
        return id == that.id &&
                Objects.equals(description, that.description) &&
                Objects.equals(implementationResponsibles, that.implementationResponsibles) &&
                Objects.equals(checkResponsibles, that.checkResponsibles) &&
                Objects.equals(protocol, that.protocol);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, description, implementationResponsibles, checkResponsibles, protocol);
    }
}
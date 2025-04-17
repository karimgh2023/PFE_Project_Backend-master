package com.PFE.DTT.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "protocolType"})
        }
)
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    // ✅ Enum-based protocol type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolType protocolType;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecificControlCriteria> specificControlCriteriaList = new ArrayList<>();

    public Protocol() {}

    public Protocol(String name, ProtocolType protocolType, User createdBy) {
        this.name = name;
        this.protocolType = protocolType;
        this.createdBy = createdBy;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<SpecificControlCriteria> getSpecificControlCriteria() {
        return specificControlCriteriaList;
    }

    public void setSpecificControlCriteriaList(List<SpecificControlCriteria> specificControlCriteriaList) {
        this.specificControlCriteriaList = specificControlCriteriaList;
    }
    @Override
    public String toString() {
        return "Protocol{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", protocolType=" + protocolType +
                ", createdBy=" + createdBy.getEmail() +
                '}';
    }
}

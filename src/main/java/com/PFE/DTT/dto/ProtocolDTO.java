package com.PFE.DTT.dto;

import com.PFE.DTT.model.Protocol;
import com.PFE.DTT.model.ProtocolType;
import com.PFE.DTT.model.User;

/**
 * Data Transfer Object for Protocol entity
 * This DTO is used to safely transfer protocol data without circular references
 */
public class ProtocolDTO {
    private int id;
    private String name;
    private ProtocolType protocolType;
    private UserDTO createdBy;

    // Default constructor
    public ProtocolDTO() {}

    // Constructor from Protocol entity
    public ProtocolDTO(Protocol protocol) {
        this.id = protocol.getId();
        this.name = protocol.getName();
        this.protocolType = protocol.getProtocolType();
        
        if (protocol.getCreatedBy() != null) {
            this.createdBy = new UserDTO(protocol.getCreatedBy());
        }
    }

    // Getters and setters
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

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Simple User Data Transfer Object 
     * Includes only the essential user information needed for protocol display
     */
    public static class UserDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private DepartmentDTO department;

        public UserDTO() {}

        public UserDTO(User user) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.role = user.getRole().name();
            
            if (user.getDepartment() != null) {
                this.department = new DepartmentDTO(user.getDepartment().getId(), user.getDepartment().getName());
            }
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public DepartmentDTO getDepartment() {
            return department;
        }

        public void setDepartment(DepartmentDTO department) {
            this.department = department;
        }
    }

    /**
     * Simple Department Data Transfer Object
     */
    public static class DepartmentDTO {
        private int id;
        private String name;

        public DepartmentDTO() {}

        public DepartmentDTO(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and setters
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
    }
} 
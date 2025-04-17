package com.PFE.DTT.controller;

import com.PFE.DTT.dto.DepartmentRequest;
import com.PFE.DTT.model.Department;
import com.PFE.DTT.model.Plant;
import com.PFE.DTT.model.User;
import com.PFE.DTT.repository.DepartmentRepository;
import com.PFE.DTT.repository.PlantRepository;
import com.PFE.DTT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PlantRepository plantRepository;
    
    @Autowired
    private UserRepository userRepository;

    // ✅ Get all departments (no auth required)
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }

    // ✅ Get department by ID
    @GetMapping("/departments/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable int id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()) {
            return ResponseEntity.ok(department.get());
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Create a new department
    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentRequest request) {
        // Validate name is not blank
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Department name is required");
        }

        // Check if department with same name already exists
        Optional<Department> existingDept = departmentRepository.findByName(request.getName());
        if (existingDept.isPresent()) {
            return ResponseEntity.badRequest().body("Department with this name already exists");
        }

        Department department = new Department(request.getName());
        Department savedDepartment = departmentRepository.save(department);
        return ResponseEntity.ok(savedDepartment);
    }

    // ✅ Update department
    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable int id, @RequestBody DepartmentRequest request) {
        // Validate name is not blank
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Department name is required");
        }

        Optional<Department> existingDept = departmentRepository.findById(id);
        if (!existingDept.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Check if name is already used by another department
        Optional<Department> deptWithSameName = departmentRepository.findByName(request.getName());
        if (deptWithSameName.isPresent() && deptWithSameName.get().getId() != id) {
            return ResponseEntity.badRequest().body("Department with this name already exists");
        }

        Department department = existingDept.get();
        department.setName(request.getName());
        Department updatedDepartment = departmentRepository.save(department);
        return ResponseEntity.ok(updatedDepartment);
    }

    // ✅ Delete department
    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable int id) {
        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        departmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ✅ Get all plants (no auth required)
    @GetMapping("/plants")
    public ResponseEntity<List<Plant>> getAllPlants() {
        List<Plant> plants = plantRepository.findAll();
        return ResponseEntity.ok(plants);
    }
    
    // ✅ Get all non-admin users
    @GetMapping("/non-admins")
    public ResponseEntity<List<User>> getAllNonAdminUsers() {
        List<User> users = userRepository.findByRoleNot(User.Role.ADMIN);

        // Optionally, remove passwords and verification code before returning
        users.forEach(user -> {
            user.setPassword(null);
            user.setVerificationCode(null);
        });

        return ResponseEntity.ok(users);
    }

    // ✅ Get users by department ID
    @GetMapping("/users/department/{departmentId}")
    public ResponseEntity<?> getUsersByDepartment(@PathVariable int departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        
        if (department.isEmpty()) {
            return ResponseEntity.badRequest().body("Department not found");
        }
        
        List<User> users = userRepository.findByDepartment(department.get());
        
        // Remove sensitive information before returning
        users.forEach(user -> {
            user.setPassword(null);
            user.setVerificationCode(null);
        });
        
        return ResponseEntity.ok(users);
    }
}

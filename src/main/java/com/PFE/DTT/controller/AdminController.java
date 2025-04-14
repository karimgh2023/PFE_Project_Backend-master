package com.PFE.DTT.controller;

import com.PFE.DTT.model.User;
import com.PFE.DTT.model.Plant;
import com.PFE.DTT.model.Department;
import com.PFE.DTT.repository.UserRepository;
import com.PFE.DTT.repository.PlantRepository;
import com.PFE.DTT.repository.DepartmentRepository;
import com.PFE.DTT.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Supprimer un compte utilisateur
    @DeleteMapping("/delete-account/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getRole() == User.Role.ADMIN) {
            return ResponseEntity.status(403).body("Suppression du compte admin non autorisée");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("Compte utilisateur supprimé avec succès");
    }

    // Récupérer toutes les usines
    @GetMapping("/plants")
    public ResponseEntity<List<Plant>> getAllPlants() {
        List<Plant> plants = plantRepository.findAll();
        return ResponseEntity.ok(plants);
    }

    // Ajouter une usine
    @PostMapping("/add-plant")
    public ResponseEntity<String> addPlant(@RequestBody Plant plant) {
        plantRepository.save(plant);
        return ResponseEntity.ok("Usine ajoutée avec succès");
    }

    // Récupérer tous les départements
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }

    // Ajouter un département
    @PostMapping("/add-department")
    public ResponseEntity<String> addDepartment(@RequestBody Department department) {
        departmentRepository.save(department);
        return ResponseEntity.ok("Département ajouté avec succès");
    }

    // Récupérer tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }



    // Promouvoir un utilisateur en Department Manager
    @PostMapping("/promote-to-department-manager")
    public ResponseEntity<String> promoteToDepartmentManager(
            @RequestHeader("Authorization") String token,
            @RequestParam String userEmail) {

        String requesterEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        logger.info("Requester email: {}", requesterEmail);

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        if (!requester.getRole().equals(User.Role.ADMIN)) {
            logger.warn("Unauthorized access attempt by: {}", requesterEmail);
            return ResponseEntity.status(403).body("Unauthorized: Only admins can promote users");
        }

        User userToPromote = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToPromote.setRole(User.Role.DEPARTMENT_MANAGER);
        userRepository.save(userToPromote);

        logger.info("User {} promoted to department manager successfully", userEmail);
        return ResponseEntity.ok("User promoted to department manager successfully. They must log out and log back in to update their role.");
    }

    // Promouvoir un utilisateur en employé
    @PostMapping("/promote-to-employee")
    public ResponseEntity<String> promoteToEmployee(
            @RequestHeader("Authorization") String token,
            @RequestParam String userEmail) {

        String requesterEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        logger.info("Requester email: {}", requesterEmail);

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        if (!requester.getRole().equals(User.Role.ADMIN)) {
            logger.warn("Unauthorized access attempt by: {}", requesterEmail);
            return ResponseEntity.status(403).body("Unauthorized: Only admins can promote users");
        }

        User userToPromote = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToPromote.setRole(User.Role.EMPLOYEE);
        userRepository.save(userToPromote);

        logger.info("User {} promoted to employee successfully", userEmail);
        return ResponseEntity.ok("User promoted to employee successfully. They must log out and log back in to update their role.");
    }
}

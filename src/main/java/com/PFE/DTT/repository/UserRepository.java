package com.PFE.DTT.repository;

import com.PFE.DTT.model.Department;
import com.PFE.DTT.model.User;
import com.PFE.DTT.model.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    
    // ✅ Get all users except admins
    List<User> findByRoleNot(Role role);
    
    // ✅ Get users by department
    List<User> findByDepartment(Department department);
}
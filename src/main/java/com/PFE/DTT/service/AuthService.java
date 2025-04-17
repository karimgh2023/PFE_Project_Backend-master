package com.PFE.DTT.service;

import com.PFE.DTT.model.User;
import com.PFE.DTT.repository.UserRepository;
import com.PFE.DTT.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private EmailService emailService; // Add EmailService

    public String updateProfilePhoto(Long userId, MultipartFile photo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newPhotoUrl = cloudinaryService.uploadProfilePhoto(photo);
        user.setProfilePhoto(newPhotoUrl);

        userRepository.save(user);

        return "Profile photo updated successfully.";
    }

    public String register(User user) {
        logger.info("Attempting to register user: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationCode(verificationCode);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.EMPLOYEE);
        user.setVerified(false);
        userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);
            logger.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}. Error: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }

        return "User registered successfully. Please check your email for the verification code.";
    }

    public String verifyUser(String verificationCode) {
        logger.info("Attempting to verify user with code: {}", verificationCode);

        User user = userRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> {
                    logger.error("Invalid verification code: {}", verificationCode);
                    return new RuntimeException("Invalid verification code");
                });

        user.setVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        logger.info("User verified successfully: {}", user.getEmail());
        return "User verified successfully!";
    }

    public String getEmailFromToken(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        return jwtUtil.extractEmail(cleanedToken);
    }

    public String updateProfilePhoto(String email, MultipartFile photo) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newPhotoUrl = cloudinaryService.uploadProfilePhoto(photo);
        user.setProfilePhoto(newPhotoUrl);

        userRepository.save(user);

        return "Profile photo updated successfully.";
    }

    public String login(String email, String password) {
        logger.info("Attempting to log in user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified");
        }

        String token = jwtUtil.generateToken(user);
        return token;
    }
}
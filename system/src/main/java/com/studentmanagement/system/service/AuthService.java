package com.studentmanagement.system.service;

import com.studentmanagement.system.dto.LoginRequest;
import com.studentmanagement.system.dto.LoginResponse;
import com.studentmanagement.system.model.User;
import com.studentmanagement.system.repository.UserRepository;
import com.studentmanagement.system.util.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    /**
     * Simple login - validates username and password
     */
    public LoginResponse login(LoginRequest request) {
        AppLogger.info("Login attempt for username: " + request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    AppLogger.error("User not found: " + request.getUsername());
                    return new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid username or password"
                    );
                });

        // Verify password
        String hashedPassword = hashPassword(request.getPassword());
        if (!user.getPassword().equals(hashedPassword)) {
            AppLogger.error("Invalid password for user: " + request.getUsername());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        AppLogger.info("Login successful for: " + request.getUsername());

        return new LoginResponse(
                user.getUsername(),
                user.getRole(),
                user.getTeacherId(),
                user.getFullName(),
                "Login successful"
        );
    }
    // Add this method to your AuthService.java

    /**
     * Create initial admin during signup - only allowed when no admins exist
     */
    public User createInitialAdmin(String username, String password, String fullName) {
        // Check if any admin already exists
        if (adminExists()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin registration is closed. System already has administrators."
            );
        }

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(hashPassword(password));
        admin.setRole("ADMIN");
        admin.setFullName(fullName);

        AppLogger.info("Creating initial admin account for: " + username);
        return userRepository.save(admin);
    }

    /**
     * Create admin user - can create multiple admins
     */
    // In AuthService.java - Keep the original createAdmin method, remove createInitialAdmin
    /**
     * Create admin user - can create multiple admins
     */
    public User createAdmin(String username, String password, String fullName) {
        // Check if username already exists (regardless of role)
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(hashPassword(password));
        admin.setRole("ADMIN");
        admin.setFullName(fullName);

        AppLogger.info("Creating admin account for: " + username);
        return userRepository.save(admin);
    }

    /**
     * Create teacher user account - can create multiple teachers
     */
    public User createTeacherUser(String teacherId, String password, String fullName) {
        // Check if username already exists (regardless of role)
        if (userRepository.findByUsername(teacherId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Teacher account with this ID already exists"
            );
        }

        User teacher = new User();
        teacher.setUsername(teacherId);  // Use teacherId as username
        teacher.setPassword(hashPassword(password));
        teacher.setRole("TEACHER");
        teacher.setTeacherId(teacherId);
        teacher.setFullName(fullName);

        AppLogger.info("Creating teacher account for: " + teacherId);
        return userRepository.save(teacher);
    }

    /**
     * Simple password hashing using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Change password
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Verify old password
        if (!user.getPassword().equals(hashPassword(oldPassword))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Current password is incorrect"
            );
        }

        user.setPassword(hashPassword(newPassword));
        userRepository.save(user);
        AppLogger.info("Password changed for user: " + username);
    }

    /**
     * Check if at least one admin exists
     */
    public boolean adminExists() {
        return userRepository.existsByRole("ADMIN");
    }

    /**
     * Get count of admin users
     */
    public long getAdminCount() {
        return userRepository.countByRole("ADMIN");
    }

    /**
     * Get count of teacher users
     */
    public long getTeacherCount() {
        return userRepository.countByRole("TEACHER");
    }
}
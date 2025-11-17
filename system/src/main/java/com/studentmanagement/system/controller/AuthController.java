package com.studentmanagement.system.controller;

import com.studentmanagement.system.dto.CreateAdminRequest;
import com.studentmanagement.system.dto.LoginRequest;
import com.studentmanagement.system.dto.LoginResponse;
import com.studentmanagement.system.model.User;
import com.studentmanagement.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    /**
     * Create admin account - can create multiple admins
     * POST /api/auth/admin/create
     */
    @PostMapping("/admin/create")
    public ResponseEntity<String> createAdmin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName) {

        authService.createAdmin(username, password, fullName);
        return ResponseEntity.ok("Admin account created successfully for: " + username);
    }
    // Add this method to your AuthController.java

    /**
     * Admin signup endpoint - allows initial admin registration
     * POST /api/auth/signup
     */
    /**
     * Admin signup endpoint - allows initial admin registration
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<String> adminSignup(@Valid @RequestBody CreateAdminRequest request) {
        authService.createAdmin(request.getUsername(), request.getPassword(), request.getFullName());
        return ResponseEntity.ok("Admin account created successfully for: " + request.getUsername());
    }

    /**
     * Create teacher account - can create multiple teachers
     * POST /api/auth/teacher/create
     */
    @PostMapping("/teacher/create")
    public ResponseEntity<String> createTeacherAccount(
            @RequestParam String teacherId,
            @RequestParam String password,
            @RequestParam String fullName) {

        authService.createTeacherUser(teacherId, password, fullName);
        return ResponseEntity.ok("Teacher account created successfully for: " + teacherId);
    }

    /**
     * Change password
     * POST /api/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        authService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    /**
     * Check if at least one admin exists
     * GET /api/auth/admin/exists
     */
    @GetMapping("/admin/exists")
    public ResponseEntity<Boolean> adminExists() {
        boolean exists = authService.adminExists();
        return ResponseEntity.ok(exists);
    }

    /**
     * Get user statistics
     * GET /api/auth/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("adminCount", authService.getAdminCount());
        stats.put("teacherCount", authService.getTeacherCount());
        return ResponseEntity.ok(stats);
    }
}
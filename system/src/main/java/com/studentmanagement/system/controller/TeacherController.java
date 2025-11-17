package com.studentmanagement.system.controller;

import com.studentmanagement.system.dto.CreateTeacherRequest;
import com.studentmanagement.system.dto.UpdateTeacherRequest;
import com.studentmanagement.system.dto.TeacherResponse;
import com.studentmanagement.system.model.Teacher;
import com.studentmanagement.system.model.TeacherMapper;
import com.studentmanagement.system.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * Create a new teacher (ADMIN only)
     */
    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(
            @RequestBody CreateTeacherRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        Teacher teacher = TeacherMapper.toEntity(request);
        Teacher createdTeacher = teacherService.createTeacher(teacher);

        return new ResponseEntity<>(TeacherMapper.toResponse(createdTeacher), HttpStatus.CREATED);
    }

    /**
     * Get all teachers (ADMIN only)
     */
    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        List<TeacherResponse> responses = teacherService.getAllTeachers()
                .stream()
                .map(TeacherMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get teacher by ID (ADMIN only)
     */
    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> getTeacher(
            @PathVariable String teacherId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        Teacher teacher = teacherService.getTeacherById(teacherId);
        return ResponseEntity.ok(TeacherMapper.toResponse(teacher));
    }

    /**
     * Update teacher details (ADMIN only)
     */
    @PutMapping("/{teacherId}")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable String teacherId,
            @RequestBody UpdateTeacherRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        Teacher updatedTeacher = teacherService.updateTeacher(teacherId, request);
        return ResponseEntity.ok(TeacherMapper.toResponse(updatedTeacher));
    }

    /**
     * Delete a teacher by teacherId (ADMIN only)
     */
    @DeleteMapping("/{teacherId}")
    public ResponseEntity<String> deleteTeacher(
            @PathVariable String teacherId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        teacherService.deleteTeacher(teacherId);
        return ResponseEntity.ok("Teacher with ID " + teacherId + " was successfully deleted.");
    }

    /**
     * Filter teachers by subject (ADMIN only)
     */
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<TeacherResponse>> getTeachersBySubject(
            @PathVariable String subject,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        List<TeacherResponse> teachers = teacherService.getTeachersBySubject(subject)
                .stream()
                .map(TeacherMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(teachers);
    }

    /**
     * Validate admin access
     */
    private void validateAdminAccess(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required"
            );
        }
    }
}
package com.studentmanagement.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentmanagement.system.dto.BulkUpdateClassRequest;
import com.studentmanagement.system.dto.CreateStudentRequest;
import com.studentmanagement.system.dto.StudentResponse;
import com.studentmanagement.system.dto.UpdateStudentRequest;
import com.studentmanagement.system.mapper.StudentMapper;
import com.studentmanagement.system.model.Students;
import com.studentmanagement.system.service.FileStorageService;
import com.studentmanagement.system.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    private final StudentService studentService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    /**
     * Create a new student with optional image upload (ADMIN only)
     * POST /api/students
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentResponse> createStudent(
            @RequestPart("student") String studentJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        try {
            // Parse JSON to CreateStudentRequest
            CreateStudentRequest request = objectMapper.readValue(studentJson, CreateStudentRequest.class);

            // Validate request manually (since @Valid doesn't work with @RequestPart String)
            // You might want to add manual validation here or use a validator

            Students student = StudentMapper.toEntity(request);

            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                String filename = fileStorageService.storeFile(image, request.getStudentId());
                student.setProfileImageUrl(filename);
            }

            Students createdStudent = studentService.createStudent(student);
            StudentResponse response = StudentMapper.toResponse(createdStudent);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to create student: " + e.getMessage()
            );
        }
    }

    /**
     * Get all students (ADMIN only)
     * GET /api/students
     */
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        List<Students> students = studentService.getAllStudents();
        List<StudentResponse> response = students.stream()
                .map(StudentMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get students by class name (ADMIN only)
     * GET /api/students/class/{className}
     */
    @GetMapping("/class/{className}")
    public ResponseEntity<List<StudentResponse>> getStudentsByClass(
            @PathVariable String className,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        List<Students> students = studentService.getStudentsByClass(className);
        List<StudentResponse> response = students.stream()
                .map(StudentMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get students assigned to a specific teacher
     * Teachers can only access their own students
     * Admins can access any teacher's students
     * GET /api/students/teacher/{teacherId}
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<StudentResponse>> getStudentsByTeacher(
            @PathVariable String teacherId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        // Teachers can only access their own students
        if ("TEACHER".equals(role) && !teacherId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access your own students"
            );
        }

        List<Students> students = studentService.getStudentsByTeacher(teacherId);
        List<StudentResponse> response = students.stream()
                .map(StudentMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get student by student ID
     * Teachers can only access their own students
     * GET /api/students/{studentId}
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudentByStudentId(
            @PathVariable String studentId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        Students student = studentService.getStudentByStudentId(studentId);

        // Teachers can only view their own students
        if ("TEACHER".equals(role)) {
            validateTeacherAccess(userId, String.valueOf(student.getTeacherId()));
        }

        if (student != null) {
            StudentResponse response = StudentMapper.toResponse(student);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Update student by student ID with optional image upload
     * ADMIN can update any student
     * TEACHER can only update their own students
     * PUT /api/students/{studentId}
     */
    @PutMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable String studentId,
            @RequestPart("student") String studentJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        try {
            // Parse JSON to UpdateStudentRequest
            UpdateStudentRequest request = objectMapper.readValue(studentJson, UpdateStudentRequest.class);

            // Teachers can only update their own students
            if ("TEACHER".equals(role)) {
                Students existingStudent = studentService.getStudentByStudentId(studentId);
                validateTeacherAccess(userId, String.valueOf(existingStudent.getTeacherId()));
            }

            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                // Get existing student to delete old image
                Students existingStudent = studentService.getStudentByStudentId(studentId);
                if (existingStudent.getProfileImageUrl() != null) {
                    fileStorageService.deleteFile(existingStudent.getProfileImageUrl());
                }

                // Upload new image
                String filename = fileStorageService.storeFile(image, studentId);
                request.setProfileImageUrl(filename);
            }

            Students updatedStudent = studentService.updateStudent(studentId, request);
            if (updatedStudent != null) {
                StudentResponse response = StudentMapper.toResponse(updatedStudent);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Failed to update student: " + e.getMessage()
            );
        }
    }

    /**
     * Delete student by student ID (ADMIN only)
     * DELETE /api/students/{studentId}
     */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteStudent(
            @PathVariable String studentId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        // Get student to delete their image
        Students student = studentService.getStudentByStudentId(studentId);
        if (student.getProfileImageUrl() != null) {
            fileStorageService.deleteFile(student.getProfileImageUrl());
        }

        studentService.deleteStudent(studentId);

        return ResponseEntity.ok(
                Map.of("message", "Student with ID " + studentId + " was successfully deleted.")
        );
    }

    /**
     * Download/view student profile image
     * GET /api/students/{studentId}/image
     */
    @GetMapping("/{studentId}/image")
    public ResponseEntity<Resource> getStudentImage(@PathVariable String studentId) {
        try {
            Students student = studentService.getStudentByStudentId(studentId);

            if (student.getProfileImageUrl() == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = fileStorageService.getFilePath(student.getProfileImageUrl());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Determine content type
                String contentType = "application/octet-stream";
                String filename = student.getProfileImageUrl();
                if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".gif")) {
                    contentType = "image/gif";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve image: " + e.getMessage()
            );
        }
    }

    /**
     * Bulk update class for students (ADMIN only)
     * PUT /api/students/bulk/class
     */
    @PutMapping("/bulk/class")
    public ResponseEntity<String> bulkUpdateClass(
            @Valid @RequestBody BulkUpdateClassRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        int updatedCount = studentService.bulkUpdateClass(
                request.getFromClassName(),
                request.getToClassName()
        );

        String message = updatedCount + " students moved from " +
                request.getFromClassName() + " to " + request.getToClassName();

        return ResponseEntity.ok(message);
    }

    /**
     * Validate that only ADMIN can access this endpoint
     */
    private void validateAdminAccess(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required"
            );
        }
    }

    /**
     * Validate that teacher can only access their own students
     */
    private void validateTeacherAccess(String teacherId, String studentTeacherId) {
        if (!teacherId.equals(studentTeacherId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access your own students"
            );
        }
    }
}
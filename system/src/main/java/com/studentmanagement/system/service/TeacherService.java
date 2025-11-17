package com.studentmanagement.system.service;

import com.studentmanagement.system.dto.UpdateTeacherRequest;
import com.studentmanagement.system.model.Teacher;
import com.studentmanagement.system.repository.TeacherRepository;
import com.studentmanagement.system.util.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    /**
     * Create a teacher
     */
    public Teacher createTeacher(Teacher teacher) {
        AppLogger.info("Creating teacher with ID: " + teacher.getTeacherId());

        // Check duplicate teacherId
        if (teacherRepository.findByTeacherId(teacher.getTeacherId()).isPresent()) {
            AppLogger.error("Duplicate teacher ID: " + teacher.getTeacherId());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Teacher ID already exists: " + teacher.getTeacherId()
            );
        }

        Teacher saved = teacherRepository.save(teacher);
        AppLogger.info("Teacher created successfully: " + saved.getTeacherId());
        return saved;
    }

    /**
     * Get all teachers
     */
    public List<Teacher> getAllTeachers() {
        AppLogger.info("Fetching all teachers");
        return teacherRepository.findAll();
    }

    /**
     * Get teacher by teacherId
     */
    public Teacher getTeacherById(String teacherId) {
        AppLogger.info("Fetching teacher by ID: " + teacherId);

        return teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> {
                    AppLogger.error("Teacher not found: " + teacherId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Teacher not found with ID: " + teacherId
                    );
                });
    }

    /**
     * Update teacher by teacherId
     */
    public Teacher updateTeacher(String teacherId, UpdateTeacherRequest updatedData) {
        AppLogger.info("Updating teacher: " + teacherId);

        Teacher existing = teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Teacher not found: " + teacherId
                        )
                );

        // Update only provided fields
        if (updatedData.getFullName() != null) existing.setFullName(updatedData.getFullName());
        if (updatedData.getEmail() != null) existing.setEmail(updatedData.getEmail());
        if (updatedData.getPhone() != null) existing.setPhone(updatedData.getPhone());
        if (updatedData.getSubject() != null) existing.setSubject(updatedData.getSubject());
        if (updatedData.getAddress() != null) existing.setAddress(updatedData.getAddress());
        if (updatedData.getHireDate() != null) existing.setHireDate(updatedData.getHireDate());
        if (updatedData.getDateOfBirth() != null) existing.setDateOfBirth(updatedData.getDateOfBirth());

        Teacher saved = teacherRepository.save(existing);
        AppLogger.info("Teacher updated successfully: " + saved.getTeacherId());

        return saved;
    }

    /**
     * Delete teacher
     */
    public boolean deleteTeacher(String teacherId) {
        AppLogger.info("Attempting to delete teacher: " + teacherId);

        Teacher teacher = teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> {
                    AppLogger.error("Cannot delete - teacher not found: " + teacherId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Cannot delete. Teacher not found: " + teacherId
                    );
                });

        teacherRepository.delete(teacher);
        AppLogger.info("Teacher deleted successfully: " + teacherId);

        return true;
    }

    /**
     * Filter teachers by subject
     */
    public List<Teacher> getTeachersBySubject(String subject) {
        AppLogger.info("Fetching teachers by subject: " + subject);
        return teacherRepository.findBySubject(subject);
    }
}

package com.studentmanagement.system.service;

import com.studentmanagement.system.dto.UpdateStudentRequest;
import com.studentmanagement.system.mapper.StudentMapper;
import com.studentmanagement.system.model.Students;
import com.studentmanagement.system.repository.StudentRepository;
import com.studentmanagement.system.repository.TeacherRepository;
import com.studentmanagement.system.util.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    /**
     * Create a new student
     */
    public Students createStudent(Students student) {
        AppLogger.info("Creating student with ID: " + student.getStudentId());

        // Check for duplicate studentId
        if (studentRepository.findByStudentId(student.getStudentId()).isPresent()) {
            AppLogger.error("Duplicate student ID: " + student.getStudentId());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student ID already exists: " + student.getStudentId()
            );
        }

        // Auto-generate full name if missing
        if (student.getFullName() == null || student.getFullName().isEmpty()) {
            student.setFullName(student.getFirstName() + " " + student.getLastName());
        }
        // Validate teacher exists
        teacherRepository.findByTeacherId(String.valueOf(student.getTeacherId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Teacher not found with ID: " + student.getTeacherId()
                ));


        Students saved = studentRepository.save(student);
        AppLogger.info("Student created successfully: " + saved.getStudentId());

        return saved;
    }


    /**
     * Get all students
     */
    public List<Students> getAllStudents() {
        AppLogger.info("Fetching all students");
        return studentRepository.findAll();
    }

    /**
     * Get students by class name
     */
    public List<Students> getStudentsByClass(String className) {
        AppLogger.info("Fetching students by class: " + className);
        return studentRepository.findByClassName(className);
    }
    /**
     * Get all students of a specific teacher
     */
    public List<Students> getStudentsByTeacher(String teacherId) {
        AppLogger.info("Fetching students for teacher: " + teacherId);

        return studentRepository.findByTeacherId(teacherId);
    }


    /**
     * Get student by student ID
     */
    public Students getStudentByStudentId(String studentId) {
        AppLogger.info("Fetching student with ID: " + studentId);

        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    AppLogger.error("Student not found: " + studentId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Student not found with ID: " + studentId
                    );
                });
    }

    /**
     * Update student by student ID using DTO
     */
    public Students updateStudent(String studentId, UpdateStudentRequest request) {
        AppLogger.info("Updating student with ID: " + studentId);

        Students existingStudent = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    AppLogger.error("Cannot update â€” student not found: " + studentId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Cannot update. Student not found: " + studentId
                    );
                });

        StudentMapper.updateEntity(existingStudent, request);

        Students updated = studentRepository.save(existingStudent);
        AppLogger.info("Student updated successfully: " + updated.getStudentId());

        return updated;
    }

    /**
     * Delete student by student ID
     */
    public boolean deleteStudent(String studentId) {
        AppLogger.info("Attempting to delete student with ID: " + studentId);

        Students student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    AppLogger.error("Cannot delete â€” student not found: " + studentId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Cannot delete. Student not found: " + studentId
                    );
                });

        studentRepository.delete(student);
        AppLogger.info("Student deleted successfully: " + studentId);

        return true;
    }

    /**
     * Bulk update class for all students in a given class
     */
    public int bulkUpdateClass(String fromClass, String toClass) {
        AppLogger.info("Bulk update: Moving students from " + fromClass + " to " + toClass);

        List<Students> students = studentRepository.findByClassName(fromClass);

        if (students.isEmpty()) {
            AppLogger.warn("No students found in class: " + fromClass);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No students found in class: " + fromClass
            );
        }

        students.forEach(student -> {
            student.setClassName(toClass);
        });

        studentRepository.saveAll(students);

        AppLogger.info("Bulk update successful. Students moved: " + students.size());
        return students.size();
    }


}
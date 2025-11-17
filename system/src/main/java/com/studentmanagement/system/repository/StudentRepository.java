package com.studentmanagement.system.repository;

import com.studentmanagement.system.model.Students;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Students, String> {

    /**
     * Find student by studentId field
     */
    Optional<Students> findByStudentId(String studentId);

    /**
     * Find all students by class name
     */
    List<Students> findByClassName(String className);

    /**
     * Check if a student exists by studentId
     */
    boolean existsByStudentId(String studentId);
    List<Students> findByTeacherId(String teacherId);

}
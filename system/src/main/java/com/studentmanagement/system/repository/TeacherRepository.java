package com.studentmanagement.system.repository;

import com.studentmanagement.system.model.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends MongoRepository<Teacher, String> {

    Optional<Teacher> findByTeacherId(String teacherId);

    List<Teacher> findBySubject(String subject);
}

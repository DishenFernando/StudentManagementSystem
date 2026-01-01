package com.studentmanagement.system.repository;

import com.studentmanagement.system.model.StudentFeeSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeSummaryRepository extends MongoRepository<StudentFeeSummary, String> {

    Optional<StudentFeeSummary> findByStudentId(String studentId);

    List<StudentFeeSummary> findByClassName(String className);

    List<StudentFeeSummary> findByTotalPendingAmountGreaterThan(Double amount);
}
package com.studentmanagement.system.repository;

import com.studentmanagement.system.model.FeeStructure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeStructureRepository extends MongoRepository<FeeStructure, String> {

    Optional<FeeStructure> findByClassName(String className);

    Optional<FeeStructure> findByClassNameAndIsActive(String className, Boolean isActive);
}
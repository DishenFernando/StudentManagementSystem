package com.studentmanagement.system.repository;

import com.studentmanagement.system.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(String role);
    long countByRole(String role);  // Add this
}
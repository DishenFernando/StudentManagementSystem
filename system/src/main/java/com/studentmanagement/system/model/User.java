package com.studentmanagement.system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;      // e.g., "admin" or "T001"
    private String password;      // Store hashed password
    private String role;          // "ADMIN" or "TEACHER"
    private String teacherId;     // Only for teachers, null for admin
    private String fullName;      // Display name
}
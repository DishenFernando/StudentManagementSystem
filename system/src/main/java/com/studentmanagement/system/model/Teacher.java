package com.studentmanagement.system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "teachers")
public class Teacher {

    @Id
    private String id;         // MongoDB ObjectId
    private String teacherId;  // Human-readable ID (e.g. T001)
    private String fullName;
    private String email;
    private String phone;
    private String subject;    // Optional
    private String address;    // Optional
    private String hireDate;  // Optional
    private String DateOfBirth; // Optional
}

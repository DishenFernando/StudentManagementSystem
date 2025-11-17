package com.studentmanagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private String id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String guardianName;
    private String guardianContact;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;
    private String enrollmentDate;
    private String className;
    private String weight;
    private String height;
    private String teacherId;

    // Image-related field
    private String profileImageUrl;
}
package com.studentmanagement.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {

    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String guardianName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Guardian contact should be 10-15 digits")
    private String guardianContact;

    private String address;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number should be 10-15 digits")
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
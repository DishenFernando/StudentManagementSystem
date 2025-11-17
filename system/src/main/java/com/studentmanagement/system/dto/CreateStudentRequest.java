package com.studentmanagement.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Guardian name is required")
    private String guardianName;

    @NotBlank(message = "Guardian contact is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Guardian contact should be 10-15 digits")
    private String guardianContact;

    private String address;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number should be 10-15 digits")
    private String phoneNumber;

    private String dateOfBirth;

    private String enrollmentDate;

    @NotBlank(message = "Class name is required")
    private String className;

    private String weight;

    private String height;

    private String teacherId;

    // Image-related fields
    private String profileImageUrl;  // URL/path to stored image
}
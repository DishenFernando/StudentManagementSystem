package com.studentmanagement.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTeacherRequest {

    @NotBlank(message = "Teacher ID is required")
    private String teacherId;     // e.g. T001

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10–15 digits")
    private String phone;

    private String subject;       // Optional

    private String address;       // Optional

    private String hireDate;      // Optional (String or LocalDate based on your choice)

    private String dateOfBirth;   // Optional
}

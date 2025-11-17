package com.studentmanagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String username;
    private String role;
    private String teacherId;  // null for admin
    private String fullName;
    private String message;
}
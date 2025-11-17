package com.studentmanagement.system.dto;

import lombok.Data;

@Data
public class UpdateTeacherRequest {

    private String fullName;
    private String email;
    private String phone;
    private String subject;
    private String address;
    private String hireDate;
    private String dateOfBirth;
}

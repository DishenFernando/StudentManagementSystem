package com.studentmanagement.system.dto;

import lombok.Data;

@Data
public class TeacherResponse {

    private String teacherId;
    private String fullName;
    private String email;
    private String phone;
    private String subject;
    private String address;
    private String hireDate;
    private String dateOfBirth;
}

package com.studentmanagement.system.mapper;

import com.studentmanagement.system.dto.CreateStudentRequest;
import com.studentmanagement.system.dto.StudentResponse;
import com.studentmanagement.system.dto.UpdateStudentRequest;
import com.studentmanagement.system.model.Students;

public class StudentMapper {

    /**
     * Convert CreateStudentRequest to Students entity
     */
    public static Students toEntity(CreateStudentRequest request) {
        Students student = new Students();
        student.setStudentId(request.getStudentId());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setFullName(request.getFullName() != null ? request.getFullName()
                : request.getFirstName() + " " + request.getLastName());
        student.setEmail(request.getEmail());
        student.setGuardianName(request.getGuardianName());
        student.setGuardianContact(request.getGuardianContact());
        student.setAddress(request.getAddress());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setClassName(request.getClassName());
        student.setWeight(request.getWeight());
        student.setHeight(request.getHeight());
        student.setTeacherId(request.getTeacherId());
        student.setProfileImageUrl(request.getProfileImageUrl());
        return student;
    }

    /**
     * Update Students entity from UpdateStudentRequest
     */
    public static void updateEntity(Students student, UpdateStudentRequest request) {
        if (request.getFirstName() != null) {
            student.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            student.setLastName(request.getLastName());
        }
        if (request.getFirstName() != null || request.getLastName() != null) {
            student.setFullName(student.getFirstName() + " " + student.getLastName());
        }
        if (request.getEmail() != null) {
            student.setEmail(request.getEmail());
        }
        if (request.getGuardianName() != null) {
            student.setGuardianName(request.getGuardianName());
        }
        if (request.getGuardianContact() != null) {
            student.setGuardianContact(request.getGuardianContact());
        }
        if (request.getAddress() != null) {
            student.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            student.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            student.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getEnrollmentDate() != null) {
            student.setEnrollmentDate(request.getEnrollmentDate());
        }
        if (request.getClassName() != null) {
            student.setClassName(request.getClassName());
        }
        if (request.getWeight() != null) {
            student.setWeight(request.getWeight());
        }
        if (request.getHeight() != null) {
            student.setHeight(request.getHeight());
        }
        if (request.getTeacherId() != null) {
            student.setTeacherId(request.getTeacherId());
        }
        if (request.getProfileImageUrl() != null) {
            student.setProfileImageUrl(request.getProfileImageUrl());
        }
    }

    /**
     * Convert Students entity to StudentResponse
     */
    public static StudentResponse toResponse(Students student) {
        return StudentResponse.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .guardianName(student.getGuardianName())
                .guardianContact(student.getGuardianContact())
                .address(student.getAddress())
                .phoneNumber(student.getPhoneNumber())
                .dateOfBirth(student.getDateOfBirth())
                .enrollmentDate(student.getEnrollmentDate())
                .className(student.getClassName())
                .weight(student.getWeight())
                .height(student.getHeight())
                .teacherId(student.getTeacherId())
                .profileImageUrl(student.getProfileImageUrl())
                .build();
    }
}
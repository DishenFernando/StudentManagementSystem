package com.studentmanagement.system.model;

import com.studentmanagement.system.dto.CreateTeacherRequest;
import com.studentmanagement.system.dto.UpdateTeacherRequest;
import com.studentmanagement.system.dto.TeacherResponse;
import com.studentmanagement.system.model.Teacher;

public class TeacherMapper {

    /**
     * Convert CreateTeacherRequest → Teacher entity
     */
    public static Teacher toEntity(CreateTeacherRequest req) {
        Teacher teacher = new Teacher();

        teacher.setTeacherId(req.getTeacherId());
        teacher.setFullName(req.getFullName());
        teacher.setEmail(req.getEmail());
        teacher.setPhone(req.getPhone());
        teacher.setSubject(req.getSubject());
        teacher.setAddress(req.getAddress());
        teacher.setHireDate(req.getHireDate());
        teacher.setDateOfBirth(req.getDateOfBirth());

        return teacher;
    }

    /**
     * Convert Teacher → TeacherResponse
     */
    public static TeacherResponse toResponse(Teacher teacher) {
        TeacherResponse res = new TeacherResponse();

        res.setTeacherId(teacher.getTeacherId());
        res.setFullName(teacher.getFullName());
        res.setEmail(teacher.getEmail());
        res.setPhone(teacher.getPhone());
        res.setSubject(teacher.getSubject());
        res.setAddress(teacher.getAddress());
        res.setHireDate(teacher.getHireDate());
        res.setDateOfBirth(teacher.getDateOfBirth());

        return res;
    }

    /**
     * Merge UpdateTeacherRequest into existing Teacher
     */
    public static void merge(Teacher teacher, UpdateTeacherRequest req) {
        if (req.getFullName() != null) teacher.setFullName(req.getFullName());
        if (req.getEmail() != null) teacher.setEmail(req.getEmail());
        if (req.getPhone() != null) teacher.setPhone(req.getPhone());
        if (req.getSubject() != null) teacher.setSubject(req.getSubject());
        if (req.getAddress() != null) teacher.setAddress(req.getAddress());
        if (req.getHireDate() != null) teacher.setHireDate(req.getHireDate());
        if (req.getDateOfBirth() != null) teacher.setDateOfBirth(req.getDateOfBirth());
    }
}

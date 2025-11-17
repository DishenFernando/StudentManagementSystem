package com.studentmanagement.system.exception;

public class StudentServiceException extends RuntimeException {

    public StudentServiceException(String message) {
        super(message);
    }

    public StudentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

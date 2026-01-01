package com.studentmanagement.system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureRequest {

    @NotNull(message = "Class name is required")
    private String className;

    @Positive(message = "Admission fee must be positive")
    private Double admissionFee;

    @Positive(message = "Monthly fee must be positive")
    private Double monthlyFee;

    @Positive(message = "Annual fee must be positive")
    private Double annualFee;

    private Double transportFee;
    private Double examFee;
    private Double activityFee;
}

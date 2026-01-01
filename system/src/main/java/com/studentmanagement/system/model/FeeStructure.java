package com.studentmanagement.system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "fee_structure")
public class FeeStructure {

    @Id
    private String id;

    private String className;  // Fee structure per class

    private Double admissionFee;
    private Double monthlyFee;
    private Double annualFee;

    // Optional: Additional fees
    private Double transportFee;
    private Double examFee;
    private Double activityFee;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
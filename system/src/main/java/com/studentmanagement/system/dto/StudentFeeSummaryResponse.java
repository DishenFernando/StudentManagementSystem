package com.studentmanagement.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeSummaryResponse {
    private String studentId;
    private String studentName;
    private String className;

    private Double admissionFeeTotal;
    private Double admissionFeePaid;
    private Double admissionFeePending;
    private Boolean admissionFeeCompleted;

    private Double totalFeesAmount;
    private Double totalPaidAmount;
    private Double totalPendingAmount;

    private LocalDateTime lastPaymentDate;
}
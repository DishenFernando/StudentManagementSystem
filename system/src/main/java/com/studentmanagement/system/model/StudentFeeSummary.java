package com.studentmanagement.system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "student_fee_summary")
public class StudentFeeSummary {

    @Id
    private String id;

    private String studentId;
    private String studentName;
    private String className;

    // Admission Fee Status
    private Double admissionFeeTotal;
    private Double admissionFeePaid;
    private Double admissionFeePending;
    private Boolean admissionFeeCompleted;

    // Monthly Fee Tracking (by month-year)
    // Key: "01-2025", Value: {total, paid, pending}
    private Map<String, MonthlyFeeDetail> monthlyFees = new HashMap<>();

    // Annual Fee Tracking (by year)
    // Key: "2025", Value: {total, paid, pending}
    private Map<String, AnnualFeeDetail> annualFees = new HashMap<>();

    // Total Summary
    private Double totalFeesAmount;
    private Double totalPaidAmount;
    private Double totalPendingAmount;

    private LocalDateTime lastPaymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class MonthlyFeeDetail {
        private Double total;
        private Double paid;
        private Double pending;
        private String status;  // PAID, PARTIAL, PENDING, OVERDUE
        private LocalDateTime dueDate;
    }

    @Data
    public static class AnnualFeeDetail {
        private Double total;
        private Double paid;
        private Double pending;
        private String status;  // PAID, PARTIAL, PENDING, OVERDUE
        private LocalDateTime dueDate;
    }
}
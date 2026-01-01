package com.studentmanagement.system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String paymentId;  // Unique payment transaction ID
    private String studentId;  // Reference to student
    private String studentName;  // For quick reference

    // Payment Type: ADMISSION, MONTHLY, ANNUAL
    private PaymentType paymentType;

    // Payment details
    private Double amountPaid;
    private Double totalAmount;  // Total fee for this payment type
    private Double pendingAmount; // Remaining amount to be paid

    // Payment Method: CASH, CARD, BANK_TRANSFER, ONLINE
    private PaymentMethod paymentMethod;

    private String transactionReference;  // Bank ref, receipt number, etc.
    private LocalDateTime paymentDate;
    private String remarks;

    // For monthly/annual payments
    private String paymentPeriod;  // e.g., "January 2025", "2025"
    private Integer month;  // 1-12 for monthly payments
    private Integer year;

    // Status: PAID, PARTIAL, PENDING, OVERDUE
    private PaymentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;  // Admin/Teacher who recorded payment

    public enum PaymentType {
        ADMISSION,
        MONTHLY,
        ANNUAL
    }

    public enum PaymentMethod {
        CASH,
        CARD,
        BANK_TRANSFER,
        ONLINE,
        CHEQUE
    }

    public enum PaymentStatus {
        PAID,      // Fully paid
        PARTIAL,   // Partially paid
        PENDING,   // Not paid yet
        OVERDUE    // Payment overdue
    }
}
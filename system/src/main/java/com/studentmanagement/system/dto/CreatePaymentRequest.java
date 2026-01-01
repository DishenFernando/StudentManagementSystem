package com.studentmanagement.system.dto;

import com.studentmanagement.system.model.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new payment.
 * Matches frontend POST /api/payments JSON structure.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    // ====== REQUIRED FIELDS ======

    @NotNull(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Payment type is required")
    private Payment.PaymentType paymentType;  // ADMISSION_FEE, MONTHLY_FEE, ANNUAL_FEE

    @NotNull(message = "Amount paid is required")
    @Positive(message = "Amount must be positive")
    private Double amountPaid;

    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod; // CASH, CARD, BANK_TRANSFER, ONLINE, CHEQUE

    // ====== OPTIONAL FIELDS ======

    private String transactionReference;

    private String remarks;

    // ====== ONLY FOR MONTHLY PAYMENTS ======
    // Included when paymentType == MONTHLY_FEE

    private Integer month;  // 1–12
    private Integer year;   // Example: 2025

    // ====== ONLY FOR ANNUAL PAYMENTS ======
    // (Year is required, but the logic in the service decides usage)
}

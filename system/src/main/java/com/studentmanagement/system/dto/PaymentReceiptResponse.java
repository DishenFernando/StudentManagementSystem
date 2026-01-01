package com.studentmanagement.system.dto;

import com.studentmanagement.system.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptResponse {
    private String receiptNumber;
    private String studentId;
    private String studentName;
    private String className;
    private Payment.PaymentType paymentType;
    private Double amountPaid;
    private Double pendingAmount;
    private Payment.PaymentMethod paymentMethod;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String remarks;
    private String receivedBy;
}
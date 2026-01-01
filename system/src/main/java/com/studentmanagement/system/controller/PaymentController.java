package com.studentmanagement.system.controller;

import com.studentmanagement.system.dto.*;
import com.studentmanagement.system.model.Payment;
import com.studentmanagement.system.model.StudentFeeSummary;
import com.studentmanagement.system.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a payment (ADMIN only)
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        validateAdminAccess(role);

        Payment payment = paymentService.processPayment(request, userId);
        PaymentResponse response = toPaymentResponse(payment);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all payments for a specific student
     * GET /api/payments/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentResponse>> getStudentPayments(
            @PathVariable String studentId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        List<Payment> payments = paymentService.getStudentPayments(studentId);
        List<PaymentResponse> response = payments.stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get fee summary for a specific student
     * GET /api/payments/student/{studentId}/summary
     */
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<StudentFeeSummaryResponse> getStudentFeeSummary(
            @PathVariable String studentId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        StudentFeeSummary summary = paymentService.getStudentFeeSummary(studentId);
        StudentFeeSummaryResponse response = toFeeSummaryResponse(summary);

        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by payment ID
     * GET /api/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable String paymentId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        Payment payment = paymentService.getPaymentById(paymentId);
        PaymentResponse response = toPaymentResponse(payment);

        return ResponseEntity.ok(response);
    }

    /**
     * Get payment receipt
     * GET /api/payments/{paymentId}/receipt
     */
    @GetMapping("/{paymentId}/receipt")
    public ResponseEntity<PaymentReceiptResponse> getPaymentReceipt(
            @PathVariable String paymentId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        validateAdminAccess(role);

        Payment payment = paymentService.getPaymentById(paymentId);
        PaymentReceiptResponse receipt = toReceiptResponse(payment);

        return ResponseEntity.ok(receipt);
    }

    // ============= HELPER METHODS =============

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentId(payment.getPaymentId())
                .studentId(payment.getStudentId())
                .studentName(payment.getStudentName())
                .paymentType(payment.getPaymentType())
                .amountPaid(payment.getAmountPaid())
                .totalAmount(payment.getTotalAmount())
                .pendingAmount(payment.getPendingAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(payment.getTransactionReference())
                .paymentDate(payment.getPaymentDate())
                .remarks(payment.getRemarks())
                .paymentPeriod(payment.getPaymentPeriod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private StudentFeeSummaryResponse toFeeSummaryResponse(StudentFeeSummary summary) {
        return StudentFeeSummaryResponse.builder()
                .studentId(summary.getStudentId())
                .studentName(summary.getStudentName())
                .className(summary.getClassName())
                .admissionFeeTotal(summary.getAdmissionFeeTotal())
                .admissionFeePaid(summary.getAdmissionFeePaid())
                .admissionFeePending(summary.getAdmissionFeePending())
                .admissionFeeCompleted(summary.getAdmissionFeeCompleted())
                .totalFeesAmount(summary.getTotalFeesAmount())
                .totalPaidAmount(summary.getTotalPaidAmount())
                .totalPendingAmount(summary.getTotalPendingAmount())
                .lastPaymentDate(summary.getLastPaymentDate())
                .build();
    }

    private PaymentReceiptResponse toReceiptResponse(Payment payment) {
        return PaymentReceiptResponse.builder()
                .receiptNumber(payment.getPaymentId())
                .studentId(payment.getStudentId())
                .studentName(payment.getStudentName())
                .paymentType(payment.getPaymentType())
                .amountPaid(payment.getAmountPaid())
                .pendingAmount(payment.getPendingAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(payment.getTransactionReference())
                .paymentDate(payment.getPaymentDate())
                .remarks(payment.getRemarks())
                .receivedBy(payment.getCreatedBy())
                .build();
    }

    private void validateAdminAccess(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required"
            );
        }
    }
}
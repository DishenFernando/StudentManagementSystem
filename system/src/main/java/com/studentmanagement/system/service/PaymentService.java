package com.studentmanagement.system.service;

import com.studentmanagement.system.dto.CreatePaymentRequest;
import com.studentmanagement.system.model.*;
import com.studentmanagement.system.repository.*;
import com.studentmanagement.system.util.AppLogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentFeeSummaryRepository feeSummaryRepository;

    /**
     * Process a payment (admission, monthly, or annual)
     */
    @Transactional
    public Payment processPayment(@Valid CreatePaymentRequest request, String createdBy) {
        AppLogger.info("Processing payment for student: " + request.getStudentId());

        // Validate student exists
        Students student = studentRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found: " + request.getStudentId()
                ));

        // Get fee structure for the student's class
        FeeStructure feeStructure = feeStructureRepository
                .findByClassNameAndIsActive(student.getClassName(), true)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fee structure not found for class: " + student.getClassName()
                ));

        // Create payment record
        Payment payment = new Payment();
        payment.setPaymentId(generatePaymentId());
        payment.setStudentId(student.getStudentId());
        payment.setStudentName(student.getFullName());
        payment.setPaymentType(request.getPaymentType());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setRemarks(request.getRemarks());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setCreatedBy(createdBy);

        // Set total amount and calculate pending based on payment type
        Double totalAmount = getTotalAmountForPaymentType(feeStructure, request.getPaymentType());
        payment.setTotalAmount(totalAmount);

        // Get or create student fee summary
        StudentFeeSummary feeSummary = feeSummaryRepository
                .findByStudentId(student.getStudentId())
                .orElseGet(() -> createNewFeeSummary(student, feeStructure));

        // Process based on payment type
        switch (request.getPaymentType()) {
            case ADMISSION:
                processAdmissionPayment(payment, feeSummary, request.getAmountPaid());
                break;
            case MONTHLY:
                processMonthlyPayment(payment, feeSummary, request, feeStructure.getMonthlyFee());
                break;
            case ANNUAL:
                processAnnualPayment(payment, feeSummary, request, feeStructure.getAnnualFee());
                break;
        }

        // Update totals in fee summary
        updateFeeSummaryTotals(feeSummary);

        // Save payment and fee summary
        Payment savedPayment = paymentRepository.save(payment);
        feeSummaryRepository.save(feeSummary);

        AppLogger.info("Payment processed successfully: " + savedPayment.getPaymentId());
        return savedPayment;
    }

    /**
     * Process admission fee payment
     */
    private void processAdmissionPayment(Payment payment, StudentFeeSummary feeSummary, Double amountPaid) {
        Double currentPaid = feeSummary.getAdmissionFeePaid();
        Double newPaidAmount = currentPaid + amountPaid;

        feeSummary.setAdmissionFeePaid(newPaidAmount);
        feeSummary.setAdmissionFeePending(feeSummary.getAdmissionFeeTotal() - newPaidAmount);

        payment.setPendingAmount(feeSummary.getAdmissionFeePending());

        if (feeSummary.getAdmissionFeePending() <= 0) {
            payment.setStatus(Payment.PaymentStatus.PAID);
            feeSummary.setAdmissionFeeCompleted(true);
            feeSummary.setAdmissionFeePending(0.0);
        } else {
            payment.setStatus(Payment.PaymentStatus.PARTIAL);
            feeSummary.setAdmissionFeeCompleted(false);
        }

        feeSummary.setLastPaymentDate(LocalDateTime.now());
    }

    /**
     * Process monthly fee payment
     */
    private void processMonthlyPayment(Payment payment, StudentFeeSummary feeSummary,
                                       CreatePaymentRequest request, Double monthlyFee) {
        if (request.getMonth() == null || request.getYear() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Month and year are required for monthly payment"
            );
        }

        String monthYearKey = String.format("%02d-%d", request.getMonth(), request.getYear());
        payment.setMonth(request.getMonth());
        payment.setYear(request.getYear());
        payment.setPaymentPeriod(getMonthName(request.getMonth()) + " " + request.getYear());

        // Get or create monthly fee detail
        StudentFeeSummary.MonthlyFeeDetail monthlyDetail = feeSummary.getMonthlyFees()
                .getOrDefault(monthYearKey, new StudentFeeSummary.MonthlyFeeDetail());

        if (monthlyDetail.getTotal() == null) {
            monthlyDetail.setTotal(monthlyFee);
            monthlyDetail.setPaid(0.0);
            monthlyDetail.setPending(monthlyFee);
            // Set due date to 5th of the month
            monthlyDetail.setDueDate(LocalDateTime.of(request.getYear(), request.getMonth(), 5, 0, 0));
        }

        // Update with new payment
        Double newPaid = monthlyDetail.getPaid() + request.getAmountPaid();
        monthlyDetail.setPaid(newPaid);
        monthlyDetail.setPending(monthlyDetail.getTotal() - newPaid);

        if (monthlyDetail.getPending() <= 0) {
            monthlyDetail.setStatus("PAID");
            monthlyDetail.setPending(0.0);
            payment.setStatus(Payment.PaymentStatus.PAID);
        } else {
            monthlyDetail.setStatus("PARTIAL");
            payment.setStatus(Payment.PaymentStatus.PARTIAL);
        }

        payment.setPendingAmount(monthlyDetail.getPending());
        feeSummary.getMonthlyFees().put(monthYearKey, monthlyDetail);
        feeSummary.setLastPaymentDate(LocalDateTime.now());
    }

    /**
     * Process annual fee payment
     */
    private void processAnnualPayment(Payment payment, StudentFeeSummary feeSummary,
                                      CreatePaymentRequest request, Double annualFee) {
        if (request.getYear() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Year is required for annual payment"
            );
        }

        String yearKey = String.valueOf(request.getYear());
        payment.setYear(request.getYear());
        payment.setPaymentPeriod("Year " + request.getYear());

        // Get or create annual fee detail
        StudentFeeSummary.AnnualFeeDetail annualDetail = feeSummary.getAnnualFees()
                .getOrDefault(yearKey, new StudentFeeSummary.AnnualFeeDetail());

        if (annualDetail.getTotal() == null) {
            annualDetail.setTotal(annualFee);
            annualDetail.setPaid(0.0);
            annualDetail.setPending(annualFee);
            annualDetail.setDueDate(LocalDateTime.of(request.getYear(), 1, 31, 0, 0));
        }

        // Update with new payment
        Double newPaid = annualDetail.getPaid() + request.getAmountPaid();
        annualDetail.setPaid(newPaid);
        annualDetail.setPending(annualDetail.getTotal() - newPaid);

        if (annualDetail.getPending() <= 0) {
            annualDetail.setStatus("PAID");
            annualDetail.setPending(0.0);
            payment.setStatus(Payment.PaymentStatus.PAID);
        } else {
            annualDetail.setStatus("PARTIAL");
            payment.setStatus(Payment.PaymentStatus.PARTIAL);
        }

        payment.setPendingAmount(annualDetail.getPending());
        feeSummary.getAnnualFees().put(yearKey, annualDetail);
        feeSummary.setLastPaymentDate(LocalDateTime.now());
    }

    /**
     * Get all payments for a student
     */
    public List<Payment> getStudentPayments(String studentId) {
        return paymentRepository.findByStudentIdOrderByPaymentDateDesc(studentId);
    }

    /**
     * Get student fee summary
     */
    public StudentFeeSummary getStudentFeeSummary(String studentId) {
        return feeSummaryRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fee summary not found for student: " + studentId
                ));
    }

    /**
     * Get payment by ID
     */
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found: " + paymentId
                ));
    }

    // ============= HELPER METHODS =============

    private StudentFeeSummary createNewFeeSummary(Students student, FeeStructure feeStructure) {
        StudentFeeSummary summary = new StudentFeeSummary();
        summary.setStudentId(student.getStudentId());
        summary.setStudentName(student.getFullName());
        summary.setClassName(student.getClassName());
        summary.setAdmissionFeeTotal(feeStructure.getAdmissionFee());
        summary.setAdmissionFeePaid(0.0);
        summary.setAdmissionFeePending(feeStructure.getAdmissionFee());
        summary.setAdmissionFeeCompleted(false);
        summary.setTotalFeesAmount(0.0);
        summary.setTotalPaidAmount(0.0);
        summary.setTotalPendingAmount(0.0);
        summary.setCreatedAt(LocalDateTime.now());
        summary.setUpdatedAt(LocalDateTime.now());
        return summary;
    }

    private void updateFeeSummaryTotals(StudentFeeSummary feeSummary) {
        Double totalPaid = feeSummary.getAdmissionFeePaid();
        Double totalPending = feeSummary.getAdmissionFeePending();

        // Add monthly fees
        for (StudentFeeSummary.MonthlyFeeDetail detail : feeSummary.getMonthlyFees().values()) {
            totalPaid += detail.getPaid();
            totalPending += detail.getPending();
        }

        // Add annual fees
        for (StudentFeeSummary.AnnualFeeDetail detail : feeSummary.getAnnualFees().values()) {
            totalPaid += detail.getPaid();
            totalPending += detail.getPending();
        }

        feeSummary.setTotalPaidAmount(totalPaid);
        feeSummary.setTotalPendingAmount(totalPending);
        feeSummary.setTotalFeesAmount(totalPaid + totalPending);
        feeSummary.setUpdatedAt(LocalDateTime.now());
    }

    private Double getTotalAmountForPaymentType(FeeStructure feeStructure, Payment.PaymentType type) {
        return switch (type) {
            case ADMISSION -> feeStructure.getAdmissionFee();
            case MONTHLY -> feeStructure.getMonthlyFee();
            case ANNUAL -> feeStructure.getAnnualFee();
        };
    }

    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return months[month - 1];
    }
}
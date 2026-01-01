package com.studentmanagement.system.repository;

import com.studentmanagement.system.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findByStudentId(String studentId);

    List<Payment> findByStudentIdAndPaymentType(String studentId, Payment.PaymentType paymentType);

    List<Payment> findByStudentIdAndMonthAndYear(String studentId, Integer month, Integer year);

    List<Payment> findByStudentIdAndYear(String studentId, Integer year);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> findByStudentIdOrderByPaymentDateDesc(String studentId);
}
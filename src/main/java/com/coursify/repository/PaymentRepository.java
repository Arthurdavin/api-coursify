package com.coursify.repository;

import com.coursify.domain.Payment;
import com.coursify.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderRef(String orderRef);

    List<Payment> findAllByStudent_Id(Long studentId);

    List<Payment> findAllByCourse_IdAndStatus(Long courseId, PaymentStatus status);

    boolean existsByStudent_IdAndCourse_IdAndStatus(Long studentId, Long courseId, PaymentStatus status);
}
package com.example.screen_golf.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByStatus(PaymentStatus status);

	Optional<Payment> findById(Long id);
}

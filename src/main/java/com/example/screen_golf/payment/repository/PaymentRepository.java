package com.example.screen_golf.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.payment.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

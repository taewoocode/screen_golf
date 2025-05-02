package com.example.screen_golf.payment.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PaymentHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus previousStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus newStatus;

	private String reason;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public PaymentHistory(Payment payment, PaymentStatus previousStatus,
		PaymentStatus newStatus, String reason) {
		this.payment = payment;
		this.previousStatus = previousStatus;
		this.newStatus = newStatus;
		this.reason = reason;
	}
} 
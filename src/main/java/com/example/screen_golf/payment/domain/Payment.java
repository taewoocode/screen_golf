package com.example.screen_golf.payment.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.user.domain.User;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne
	@JoinColumn(name = "reservation_id", nullable = false)
	private Reservation reservation;

	@Column(nullable = false)
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	private String paymentMethod;

	private String transactionId;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Payment(User user, Reservation reservation, Integer amount, String paymentMethod) {
		this.user = user;
		this.reservation = reservation;
		this.amount = amount;
		this.status = PaymentStatus.PENDING;
		this.paymentMethod = paymentMethod;
	}

	public void completePayment(String transactionId) {
		this.status = PaymentStatus.COMPLETED;
		this.transactionId = transactionId;
	}

	public void failPayment() {
		this.status = PaymentStatus.FAILED;
	}

	public void refund() {
		this.status = PaymentStatus.REFUNDED;
	}

	/**
	 * ============================================================
	 *                         PaymentDTO
	 * ============================================================
	 */

	/**
	 * // 결제 응답 DTO: 결제 처리 후 클라이언트가 요청하는 정가
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentRequest {
		private Long reservationId;
		private Long userId;
		private Integer amount;
		private String paymentMethod;
	}

	/**
	 * // 결제 응답 DTO: 결제 처리 후 클라이언트에 반환하는 정보
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentResponse {
		private Long paymentId;       // 생성된 Payment 엔티티의 식별자
		private Long reservationId;
		private Long userId;
		private Integer amount;
		private String paymentMethod;
		private String status;        // PaymentStatus의 값 (예: PENDING, COMPLETED, FAILED, REFUNDED)
		private String transactionId; // 결제 완료 후 외부 결제 시스템에서 발급한 거래 식별자, 있을 경우
		private LocalDateTime createdAt;
	}
} 
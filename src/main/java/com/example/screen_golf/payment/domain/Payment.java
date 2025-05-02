package com.example.screen_golf.payment.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.coupon.domain.UserCoupon;
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
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "reservation_id")
	private Reservation reservation;

	@ManyToOne
	@JoinColumn(name = "user_coupon_id")
	private UserCoupon userCoupon;

	@Column(nullable = false)
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod;

	@Column(name = "transaction_id")
	private String transactionId;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Payment(User user, Reservation reservation, Integer amount,
		PaymentStatus status, String paymentMethod, String transactionId) {
		this.user = user;
		this.reservation = reservation;
		this.amount = amount;
		this.status = status;
		this.paymentMethod = paymentMethod;
		this.transactionId = transactionId;
		this.createdAt = LocalDateTime.now();
	}

	public void updateStatus(PaymentStatus status, String transactionId) {
		this.status = status;
		this.transactionId = transactionId;
	}

	/**
	 * 정적 팩토리 메서드를 통해 Payment 객체를 생성합니다.
	 * 초기 상태는 PENDING으로 설정됩니다.
	 */
	public static Payment createPayment(Long user, Reservation reservation, Integer amount, String paymentMethod,
		UserCoupon userCoupon) {
		Payment payment = new Payment();
		payment.reservation = reservation;
		payment.amount = amount;
		payment.paymentMethod = paymentMethod;
		payment.status = PaymentStatus.PENDING;
		payment.userCoupon = userCoupon;
		return payment;
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
}

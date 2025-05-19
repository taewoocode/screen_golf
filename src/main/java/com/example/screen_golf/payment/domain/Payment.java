package com.example.screen_golf.payment.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.coupon.domain.Coupon;
import com.example.screen_golf.room.domain.Room;
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
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@OneToOne
	@JoinColumn(name = "user_coupon_id")
	private Coupon coupon;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	private String paymentMethod;

	@Column
	private String paymentKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Column
	private String message;

	@Column(name = "reservation_start_time")
	private LocalDateTime reservationStartTime;

	@Column(name = "reservation_end_time")
	private LocalDateTime reservationEndTime;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Payment(User user, Room room, Integer amount, PaymentStatus status, Coupon coupon,
		String paymentMethod, String message, LocalDateTime reservationStartTime, LocalDateTime reservationEndTime) {
		this.user = user;
		this.room = room;
		this.amount = amount;
		this.status = status;
		this.coupon = coupon;
		this.paymentMethod = paymentMethod;
		this.message = message;
		this.reservationStartTime = reservationStartTime;
		this.reservationEndTime = reservationEndTime;
	}

	public void complete() {
		this.status = PaymentStatus.COMPLETED;
	}

	public PaymentStatus fail() {
		return PaymentStatus.FAILED;
	}

	// public void refund() {
	// 	this.status = PaymentStatus.CANCELED;
	// }

	public void completePayment(String paymentKey) {
		this.status = PaymentStatus.COMPLETED;
		this.paymentKey = paymentKey;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(PaymentStatus paymentStatus) {
		this.status = paymentStatus;
	}

}

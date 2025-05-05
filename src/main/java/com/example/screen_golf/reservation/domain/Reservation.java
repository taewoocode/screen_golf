package com.example.screen_golf.reservation.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.payment.domain.Payment;
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
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status;

	@OneToOne
	@JoinColumn(name = "payment_id")
	private Payment payment;

	private String memo;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Reservation(User user, Room room, LocalDateTime startTime, LocalDateTime endTime, String memo,
		ReservationStatus status, Payment payment) {
		this.user = user;
		this.room = room;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.memo = memo;
		this.payment = payment;
	}

	public void validateUserPassword(User user) {
		user.isValidPassword();  // User의 비밀번호 검증 메서드 호출
	}

	public void updateReservation(LocalDateTime startTime, LocalDateTime endTime, String memo) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.memo = memo;
	}

	public void changeStatus(ReservationStatus newStatus) {
		this.status = newStatus;
	}

	public Integer calculateTotalAmount() {
		long hours = ChronoUnit.HOURS.between(startTime, endTime);
		return (int)(hours * room.getPricePerHour());
	}

	public boolean isAvailableForReservation() {
		return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
	}

	public boolean isCancellable() {
		return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
	}

	public void cancel() {
		if (!isCancellable()) {
			throw new IllegalStateException("취소할 수 없는 예약 상태입니다.");
		}
		this.status = ReservationStatus.CANCELLED;
	}

	public void confirm() {
		if (status != ReservationStatus.PENDING) {
			throw new IllegalStateException("확정할 수 없는 예약 상태입니다.");
		}
		this.status = ReservationStatus.CONFIRMED;
	}

	public void complete() {
		if (status != ReservationStatus.CONFIRMED) {
			throw new IllegalStateException("완료할 수 없는 예약 상태입니다.");
		}
		this.status = ReservationStatus.COMPLETED;
	}
} 
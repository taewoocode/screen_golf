package com.example.screen_golf.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status;

	private String memo;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Reservation(User user, Room room, LocalDateTime startTime, LocalDateTime endTime, String memo) {
		this.user = user;
		this.room = room;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = ReservationStatus.PENDING;
		this.memo = memo;
	}

	public void updateReservation(LocalDateTime startTime, LocalDateTime endTime, String memo) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.memo = memo;
	}

	public void changeStatus(ReservationStatus status) {
		this.status = status;
	}

	// =====================================
	// 예약 관련 DTO (static inner classes)
	// =====================================

	/**
	 * 예약 진행을 위한 요청 DTO
	 * 클라이언트는 예약 가능한 방을 선택한 후 이 정보를 이용해 예약을 진행할 수 있다.
	 * 운영시간(11:00 ~ 22:00) 내에서 예약을 진행해야 하며, 예약 진행 날짜와 시간이 제공된다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationBookingRequest {
		private Long userId;
		private Long roomId; // 예약 가능 방 검색 후 선택된 방의 ID
		private LocalDate date;
		private LocalTime startTime;
		private LocalTime endTime;
		private String memo;

		/**
		 * 예약 요청 정보를 LocalDateTime으로 변환한다.
		 */
		public LocalDateTime getReservationStartDateTime() {
			return LocalDateTime.of(date, startTime);
		}

		public LocalDateTime getReservationEndDateTime() {
			return LocalDateTime.of(date, endTime);
		}

		/**
		 * 운영시간(11:00 ~ 22:00) 내에 예약 시간이 유효한지 검증한다.
		 */
		public void validateOperatingHours() {
			LocalTime openingTime = LocalTime.of(11, 0);
			LocalTime closingTime = LocalTime.of(22, 0);
			if (startTime.isBefore(openingTime)) {
				throw new IllegalArgumentException("예약 시작 시간은 운영시간인 11시 이후여야 합니다.");
			}
			if (endTime.isAfter(closingTime)) {
				throw new IllegalArgumentException("예약 종료 시간은 운영시간인 22시 이전이어야 합니다.");
			}
			if (!startTime.isBefore(endTime)) {
				throw new IllegalArgumentException("예약 시작 시간은 종료 시간보다 이전이어야 합니다.");
			}
		}
	}

	/**
	 * 예약 가능한 방 검색 요청 DTO
	 * 사용자가 원하는 날짜, 예약 시간대, 및 원하는 룸 타입 정보를 제공하여
	 * 해당 조건에 부합하는 예약 가능한 방들을 조회할 수 있도록 한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationSearchRequest {
		private LocalDate date;
		private LocalTime desiredStartTime;
		private LocalTime desiredEndTime;
		private RoomType roomType; // STANDARD, PREMIUM, VIP 등

		public LocalDateTime getReservationStartDateTime() {
			return LocalDateTime.of(date, desiredStartTime);
		}

		public LocalDateTime getReservationEndDateTime() {
			return LocalDateTime.of(date, desiredEndTime);
		}

		/**
		 * 운영시간(11:00 ~ 22:00) 내에 검색 시간이 유효한지 검증한다.
		 */
		public void validateOperatingHours() {
			LocalTime openingTime = LocalTime.of(11, 0);
			LocalTime closingTime = LocalTime.of(22, 0);
			if (desiredStartTime.isBefore(openingTime)) {
				throw new IllegalArgumentException("검색 시작 시간은 운영시간인 11시 이후여야 합니다.");
			}
			if (desiredEndTime.isAfter(closingTime)) {
				throw new IllegalArgumentException("검색 종료 시간은 운영시간인 22시 이전이어야 합니다.");
			}
			if (!desiredStartTime.isBefore(desiredEndTime)) {
				throw new IllegalArgumentException("검색 시작 시간은 종료 시간보다 이전이어야 합니다.");
			}
		}
	}

	/**
	 * 예약 진행 결과 응답 DTO
	 * 예약 생성 후, 예약 내역과 함께 해당 예약에 포함된 방의 상세 정보(이름, 타입, 가격 등)를 반환한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ReservationResponse {
		private Long reservationId;
		private Long userId;
		private Long roomId;
		private String roomName;
		private RoomType roomType;
		private Integer pricePerHour;
		private LocalDateTime startTime;
		private LocalDateTime endTime;
		private ReservationStatus reservationStatus;
		private String memo;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static ReservationResponse fromEntity(Reservation reservation) {
			return ReservationResponse.builder()
				.reservationId(reservation.getId())
				.userId(reservation.getUser().getId())
				.roomId(reservation.getRoom().getId())
				.roomName(reservation.getRoom().getName())
				.roomType(reservation.getRoom().getRoomType()) // Room 엔티티에 roomType 필드가 있어야 함
				.pricePerHour(reservation.getRoom().getPricePerHour())
				.startTime(reservation.getStartTime())
				.endTime(reservation.getEndTime())
				.reservationStatus(reservation.getStatus())
				.memo(reservation.getMemo())
				.createdAt(reservation.getCreatedAt())
				.updatedAt(reservation.getUpdatedAt())
				.build();
		}
	}

	/**
	 * 예약 가능한 방 검색 결과 응답 DTO
	 * 예약 검색 결과로, 클라이언트에 각 방의 상세 정보(이름, 타입, 상태, 가격 및 설명 등)를 제공한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class AvailableRoomResponse {
		private Long roomId;
		private String roomName;
		private RoomType roomType;
		private RoomStatus roomStatus;
		private Integer pricePerHour;
		private String description;

		public static AvailableRoomResponse fromRoom(Room room) {
			return AvailableRoomResponse.builder()
				.roomId(room.getId())
				.roomName(room.getName())
				.roomType(room.getRoomType())
				.roomStatus(room.getStatus())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.build();
		}
	}
} 
package com.example.screen_golf.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReservationCreateInfo {

	/**
	 * 예약 진행 결과 응답 DTO
	 * 예약 생성 후, 예약 내역과 함께 해당 예약에 포함된 방의 상세 정보(이름, 타입, 가격 등)를 반환한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ReservationCreateResponse {
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

		public static ReservationCreateResponse fromEntity(Reservation reservation) {
			return ReservationCreateResponse.builder()
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
	 * 예약 진행을 위한 요청 DTO
	 * 클라이언트는 예약 가능한 방을 선택한 후 이 정보를 이용해 예약을 진행할 수 있다.
	 * 운영시간(11:00 ~ 22:00) 내에서 예약을 진행해야 하며, 예약 진행 날짜와 시간이 제공된다.
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationCreateRequest {
		private Long userId;
		private LocalDate date;
		private RoomType roomType;        // 예약할 때 선택한 룸 타입 (예: STANDARD, PREMIUM, VIP)
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
}

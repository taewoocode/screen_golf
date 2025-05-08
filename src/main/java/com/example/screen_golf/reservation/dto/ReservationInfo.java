package com.example.screen_golf.reservation.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReservationInfo {

	/**
	 * 예약 가능한 방 조회 요청 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AvailableRoomsRequest {
		private LocalDateTime date;      // 조회할 날짜
		private RoomType roomType;       // 방 타입 (선택사항)
		@Builder.Default
		private int userCount = 1;       // 기본 사용자 수: 1명
	}

	/**
	 * 예약 가능한 시간 조회 요청 DTO
	 */

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AvailableTimeSlotsRequest {
		private Long roomId;             // 방 ID
		private LocalDateTime date;      // 조회할 날짜
	}

	/**
	 * 예약 생성 요청 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationRequest {
		private Long userId;             // 사용자 ID
		private Long roomId;             // 방 ID
		private LocalDateTime startTime; // 시작 시간
		private LocalDateTime endTime;   // 종료 시간
		private Long paymentId;          // 결제 ID (선택사항)
	}

	/**
	 * 예약 응답 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationResponse {
		private Long reservationId;      // 예약 ID
		private Long roomId;             // 방 ID
		private String roomName;         // 방 이름
		private LocalDateTime startTime; // 시작 시간
		private LocalDateTime endTime;   // 종료 시간
		private ReservationStatus status; // 예약 상태
		private Integer totalAmount;     // 총 금액
		private boolean paymentRequired; // 결제 필요 여부
		private String message;          // 추가 메시지

		public static ReservationResponse toDto(Reservation reservation) {
			return ReservationResponse.builder()
				.reservationId(reservation.getId())
				.roomId(reservation.getRoom().getId())
				.roomName(reservation.getRoom().getName())
				.startTime(reservation.getStartTime())
				.endTime(reservation.getEndTime())
				.status(reservation.getStatus())
				.totalAmount(reservation.calculateTotalAmount())
				.paymentRequired(reservation.getStatus() == ReservationStatus.PENDING)
				.message(reservation.getStatus() == ReservationStatus.PENDING ?
					"결제가 필요합니다. 결제를 진행해주세요." :
					"예약이 완료되었습니다.")
				.build();
		}
	}

	/**
	 * 예약 가능한 방 응답 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AvailableRoomResponse {
		private Long roomId;             // 방 ID
		private String roomName;         // 방 이름
		private RoomType roomType;       // 방 타입
		private Integer pricePerHour;    // 시간당 가격
		private String description;      // 방 설명
	}

	/**
	 * 예약 가능한 시간대 응답 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AvailableTimeSlotResponse {
		private LocalDateTime startTime; // 시작 시간
		private LocalDateTime endTime;   // 종료 시간
		private boolean available;       // 예약 가능 여부
	}
} 
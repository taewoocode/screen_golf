package com.example.screen_golf.reservation.dto;

import java.time.format.DateTimeFormatter;

import com.example.screen_golf.reservation.domain.Reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 ID 기반 예약 조회 요청/응답 DTO
 */
public class ReservationSearchIdInfo {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationSearchIdRequest {
		private Long userId;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ReservationSearchIdResponse {
		private Long reservationId;
		private String roomName;
		private String status;
		private String memo;
		private String startTime;
		private String endTime;

		public static ReservationSearchIdResponse convertReservationDto(Reservation reservation) {
			return ReservationSearchIdResponse.builder()
				.reservationId(reservation.getId())
				.roomName(reservation.getRoom().getName())
				.status(reservation.getStatus().name())
				.memo(reservation.getMemo())
				.startTime(reservation.getStartTime().format(FORMATTER))
				.endTime(reservation.getEndTime().format(FORMATTER))
				.build();
		}
	}
}

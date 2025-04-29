package com.example.screen_golf.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReservationAvailableInfo {

	/**
	 * 예약 가능한 방 검색 요청 DTO
	 * 사용자가 원하는 날짜, 예약 시간대, 및 원하는 룸 타입 정보를 제공하여
	 * 해당 조건에 부합하는 예약 가능한 방들을 조회할 수 있도록 한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReservationAvailableSearchRequest {
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
	 * 예약 가능한 방 검색 결과 응답 DTO
	 * 예약 검색 결과로, 클라이언트에 각 방의 상세 정보(이름, 타입, 상태, 가격 및 설명 등)를 제공한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ReservationAvaliableSearchResponse {
		private Long roomId;
		private String roomName;
		private RoomType roomType;
		private RoomStatus roomStatus;
		private Integer pricePerHour;
		private String description;

		public static ReservationAvaliableSearchResponse fromRoom(Room room) {
			return ReservationAvaliableSearchResponse.builder()
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

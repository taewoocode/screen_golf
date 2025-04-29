package com.example.screen_golf.room.dto;

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

public class RoomUpdateInfo {

	/**
	 * Room 수정 요청 DTO
	 * 클라이언트가 수정할 수 있는 값:
	 * - 예약 날짜, 예약 시작 시간, 이용 시간, Room 타입, Room 이름, 사용자 수, 가격, 설명 등
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class RoomUpdateRequest {
		private Long id;
		private String name;
		private LocalDate reservationDate;
		private LocalTime startTime;
		private Integer usageDurationInHours;
		private RoomType roomType;
		private Integer pricePerHour;
		private Integer userCount;
		private String description;
	}

	/**
	 * Room 수정 응답 DTO
	 * Room 엔티티 생성 후, 응답으로 반환되는 값들을 담습니다.
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomUpdateResponse {
		private Long id;
		private String name;
		private RoomStatus status;
		private RoomType roomType;
		private Integer pricePerHour;
		private String description;
		private LocalDate reservationDate;
		private LocalTime startTime;
		private LocalTime endTime;
		private Integer userCount;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static RoomUpdateResponse fromEntity(Room room) {
			return RoomUpdateResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.status(room.getStatus())
				.roomType(room.getRoomType())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.reservationDate(room.getReservationDate())
				.startTime(room.getStartTime())
				.endTime(room.getEndTime())
				.userCount(room.getUserCount())
				.createdAt(room.getCreatedAt())
				.updatedAt(room.getUpdatedAt())
				.build();
		}
	}
}

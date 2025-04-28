package com.example.screen_golf.room.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RoomFindInfo {

	/**
	 * Room 조회 응답 DTO
	 */
	@Getter
	@AllArgsConstructor
	public static class RoomFindResponse {
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

		public static RoomFindResponse fromEntity(Room room) {
			return new RoomFindResponse(
				room.getId(),
				room.getName(),
				room.getStatus(),
				room.getRoomType(),
				room.getPricePerHour(),
				room.getDescription(),
				room.getReservationDate(),
				room.getStartTime(),
				room.getEndTime(),
				room.getUserCount()
			);
		}
	}
}

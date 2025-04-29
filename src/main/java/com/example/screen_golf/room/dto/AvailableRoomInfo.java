package com.example.screen_golf.room.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AvailableRoomInfo {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class AvailableRoomRequest {
		private LocalDate reservationDate;
		private LocalTime startTime;
		private int durationInHours;
		private Integer userCount;
		private RoomType roomType;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class AvailableRoomResponse {
		private Long roomId;
		private String roomName;
		private RoomType roomType;
		private RoomStatus roomStatus;
		private Integer pricePerHour;
		private String description;
	}

}

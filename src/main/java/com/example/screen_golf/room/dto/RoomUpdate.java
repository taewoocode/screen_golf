package com.example.screen_golf.room.dto;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoomUpdate {

	/**
	 * Room 수정 요청 DTO
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomUpdateRequest {
		private RoomStatus roomStatus;
		private String name;
		private Integer pricePerHour;
		private String description;

		/**
		 * DTO -> 엔티티 적용
		 */
		public void apply(Room room) {
			room.updateRoomInfo(name, pricePerHour, description);
			room.changeStatus(roomStatus);
		}
	}

	/**
	 * Room 수정 응답 DTO
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomUpdateResponse {
		private Long id;
		private RoomStatus roomStatus;
		private String name;
		private Integer pricePerHour;
		private String description;
	}
}

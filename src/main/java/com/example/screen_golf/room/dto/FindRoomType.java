package com.example.screen_golf.room.dto;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FindRoomType {

	/**
	 * Room 타입으로 조회 요청 DTO
	 * 클라이언트가 특정 Room 타입을 기반으로 조회할 때 사용
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomTypeRequest {
		private RoomType roomType;
	}

	/**
	 * Room 타입으로 조회 응답 DTO
	 * 요청한 RoomType에 해당하는 Room 목록 반환
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomTypeResponse {
		private Long id;
		private String name;
		private RoomStatus status;
		private Integer pricePerHour;
		private String description;

		public static RoomTypeResponse fromEntity(Room room) {
			return RoomTypeResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.status(room.getStatus())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.build();
		}
	}
}

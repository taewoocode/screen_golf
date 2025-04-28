package com.example.screen_golf.room.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

import lombok.Builder;
import lombok.Getter;

public class RoomCreateInfo {

	/**
	 * Room 생성 요청 DTO
	 * 클라이언트로부터 생성할 데이터만 받아 엔티티로 변환하는 역할을 합니다.
	 */
	@Getter
	@Builder
	public static class RoomCreateRequest {
		private Long id;
		private String name;
		private RoomType roomType;
		private Integer pricePerHour;
		private String description;

		// DTO -> Entity 변환 메서드, 기본 상태는 AVAILABLE로 설정
		public Room toEntity(RoomStatus status) {
			return Room.builder()
				.name(this.name)
				.status(status)
				.roomType(this.roomType)
				.pricePerHour(this.pricePerHour)
				.description(this.description)
				.build();
		}
	}

	@Getter
	@Builder
	public static class RoomResponse {
		private Long id;
		private String name;
		private RoomStatus status;
		private RoomType roomType;
		private Integer pricePerHour;
		private String description;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static RoomResponse fromEntity(Room room) {
			return RoomResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.status(room.getStatus())
				.roomType(room.getRoomType())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.createdAt(room.getCreatedAt())
				.updatedAt(room.getUpdatedAt())
				.build();
		}
	}
}

package com.example.screen_golf.room.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoomCreateInfo {

	/**
	 * Room 생성 요청 DTO
	 * 클라이언트가 보내야 하는 값:
	 * - 예약 날짜 (예: 2025-05-01)
	 * - 예약 시작 시간 (예: 11:00 ~ 23:00 사이)
	 * - 이용 시간(몇 시간 이용하는지)
	 * - Room 타입, Room 이름, 사용자 수, 가격, 설명 등
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class RoomCreateRequest {

		@NotBlank(message = "Room 이름은 필수입니다.")
		private String name;

		@NotNull(message = "예약 날짜는 필수입니다.")
		@FutureOrPresent(message = "예약 날짜는 현재 또는 미래여야 합니다.")
		private LocalDate reservationDate;

		@NotNull(message = "예약 시작 시간은 필수입니다.")
		private LocalTime startTime;

		@NotNull(message = "이용 시간(시간 단위)은 필수입니다.")
		private Integer usageDurationInHours;

		@NotNull(message = "RoomType은 필수입니다.")
		private RoomType roomType;

		@NotNull(message = "가격은 필수입니다.")
		private Integer pricePerHour;

		@NotNull(message = "사용자 수는 필수입니다.")
		private Integer userCount;

		private String description;

		public static Room toEntity(RoomCreateInfo.RoomCreateRequest createRequest) {
			Room room = Room.builder()
				.name(createRequest.getName())
				.status(RoomStatus.AVAILABLE) // 새로 생성할 때는 기본적으로 AVAILABLE로 설정
				.roomType(createRequest.getRoomType())
				.pricePerHour(createRequest.getPricePerHour())
				.description(createRequest.getDescription())
				.build();
			return room;
		}
	}

	/**
	 * Room 생성 응답 DTO
	 * Room 엔티티 생성 후, 응답으로 반환되는 값들을 담습니다.
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomCreateResponse {
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

		public static RoomCreateResponse fromEntity(Room room) {
			return RoomCreateResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.status(room.getStatus())
				.roomType(room.getRoomType())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.reservationDate(room.getReservationDate())
				.startTime(room.getStartTime())
				.endTime(room.getEndTime())
				.userCount(room.getCapacity())
				.createdAt(room.getCreatedAt())
				.updatedAt(room.getUpdatedAt())
				.build();
		}
	}
}

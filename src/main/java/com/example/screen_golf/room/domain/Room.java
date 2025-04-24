package com.example.screen_golf.room.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.reservation.domain.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RoomStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RoomType roomType;

	@Column(nullable = false)
	private Integer pricePerHour;

	private String description;

	@OneToMany(mappedBy = "room")
	private List<Reservation> reservations = new ArrayList<>();

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Room(String name, RoomStatus status, Integer pricePerHour, String description) {
		this.name = name;
		this.status = status;
		this.pricePerHour = pricePerHour;
		this.description = description;
	}

	public void updateRoomInfo(String name, Integer pricePerHour, String description) {
		this.name = name;
		this.pricePerHour = pricePerHour;
		this.description = description;
	}

	public void changeStatus(RoomStatus status) {
		this.status = status;
	}

	/**
	 * ======================================================================
	 * 								Room DTO
	 * ======================================================================
	 */

	/**
	 * Room조회 Dto
	 */
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
		private LocalDateTime updateAt;

		public static RoomResponse fromEntity(Room room) {
			return RoomResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.status(room.getStatus())
				.roomType(room.getRoomType())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.createdAt(room.getCreatedAt())
				.updateAt(room.getUpdatedAt())
				.build();
		}
	}

	/**
	 * Room 생성 요청 DTO
	 */
	@Getter
	@Builder
	public static class RoomCreateRequest {
		private String name;
		private RoomType roomType;
		private Integer pricePerHour;
		private String description;

		// DTO -> Entity 변환 메서드
		public Room toEntity() {
			return Room.builder()
				.name(this.name)
				.status(RoomStatus.AVAILABLE)
				.pricePerHour(this.pricePerHour)
				.description(this.description)
				.build();
		}
	}

	/**
	 * RoomType으로 조회
	 */
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomTypeRequest {
		private RoomType roomType;
	}
}
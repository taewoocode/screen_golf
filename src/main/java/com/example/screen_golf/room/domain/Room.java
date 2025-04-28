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
	public Room(String name, RoomStatus status, RoomType roomType, Integer pricePerHour, String description) {

		this.name = name;
		this.status = status;
		this.roomType = roomType;
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

	public boolean canChangeStatusTo(RoomStatus newStatus) {
		if (this.status == newStatus)
			return true; // 같은 상태면 허용

		return switch (this.status) {
			case AVAILABLE -> newStatus == RoomStatus.RESERVED || newStatus == RoomStatus.MAINTENANCE;
			case RESERVED -> newStatus == RoomStatus.IN_USE || newStatus == RoomStatus.AVAILABLE;
			case IN_USE -> newStatus == RoomStatus.AVAILABLE || newStatus == RoomStatus.MAINTENANCE;
			case MAINTENANCE -> newStatus == RoomStatus.AVAILABLE;
		};
	}

	/**
	 * Room 수정 요청 DTO
	 * 업데이트 시 계층 간 전송할 데이터를 정의하며, 엔티티의 특정 필드를 변경할 때 사용됩니다.
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomUpdateRequest {
		private Long id;
		private RoomStatus roomStatus;
		private String name;
		private Integer pricePerHour;
		private String description;

		/**
		 * 해당 DTO의 값을 Room 엔티티에 반영하는 헬퍼 메서드
		 */
		public void apply(Room room) {
			room.updateRoomInfo(this.name, this.pricePerHour, this.description);
			room.updateStatus(this.roomStatus);

		}
	}

	private void updateStatus(RoomStatus roomStatus) {
		this.status = roomStatus;
	}

	/**
	 * Room 수정 응답 DTO
	 * Room 수정 후의 결과 정보를 담습니다.
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
	 * Room 삭제 응답
	 * 삭제 작업 후 클라이언트에 제공할 정보
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomDeleteResponse {
		private Long id;
		private String message;

		public static RoomDeleteResponse fromEntity(Room room) {
			return RoomDeleteResponse.builder()
				.id(room.getId())
				.message(room.getId() + ": 룸 변경 완료")
				.build();
		}
	}

	/**
	 * Room 삭제 요청
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomDeleteRequest {
		private Long id;
		private String message;

		public static RoomDeleteResponse fromEntity(Room room) {
			return RoomDeleteResponse.builder()
				.id(room.getId())
				.message(room.getId() + ": 룸 삭제 완료")
				.build();
		}
	}

}

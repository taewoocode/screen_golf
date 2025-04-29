package com.example.screen_golf.room.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room {

	/**
	 * Room_id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Room_name
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * Room 상태
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RoomStatus status;

	/**
	 * Room_타입 - Standard, Premium, VIP
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RoomType roomType;

	/**
	 * 시간당 가격
	 */
	@Column(nullable = false)
	private Integer pricePerHour;

	/**
	 * 설명
	 */
	private String description;

	/**
	 * 예약 날짜
	 */
	// 예약 관련 추가 필드
	@Column(nullable = false)
	private LocalDate reservationDate;

	/**
	 * 예약 시간
	 */
	@Column(nullable = false)
	private LocalTime startTime;

	/**
	 * 예약 종료시간
	 */
	// 예약 종료 시간은 startTime + usageDurationInHours에서 계산
	@Column(nullable = false)
	private LocalTime endTime;

	/**
	 * 사용자
	 */
	// 예약 또는 Room 사용 시의 인원 정보
	@Column(nullable = false)
	private Integer userCount;

	@OneToMany(mappedBy = "room")
	private List<Reservation> reservations = new ArrayList<>();

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Room(String name, RoomStatus status, RoomType roomType, Integer pricePerHour, String description,
		LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Integer userCount) {
		this.name = name;
		this.status = status;
		this.roomType = roomType;
		this.pricePerHour = pricePerHour;
		this.description = description;
		this.reservationDate = reservationDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userCount = userCount;
	}

	public void updateRoomInfo(String name, Integer pricePerHour, String description) {
		this.name = name;
		this.pricePerHour = pricePerHour;
		this.description = description;
	}

	// 상태 반환 메서드 추가
	public RoomStatus getRoomStatus() {
		return this.status;
	}

	public void updateRoomName(String name) {
		this.name = name;
	}

	public void updateReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public void updateStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void updateEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public void updateRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

	public void updateRoomPrice(Integer pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public void updateUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public void updateRoomDescription(String description) {
		this.description = description;
	}

	public void updateRoomTime(LocalTime startTime, int durationInHours) {
		this.startTime = startTime;
		this.endTime = startTime.plusHours(durationInHours);
	}

	private void updateStatus(RoomStatus roomStatus) {
		this.status = roomStatus;
	}

	public void changeStatus(RoomStatus status) {
		this.status = status;
	}

	public boolean canChangeStatusTo(RoomStatus newStatus) {
		if (this.status == newStatus)
			return true;

		return switch (this.status) {
			case AVAILABLE -> newStatus == RoomStatus.RESERVED || newStatus == RoomStatus.MAINTENANCE;
			case RESERVED -> newStatus == RoomStatus.IN_USE || newStatus == RoomStatus.AVAILABLE;
			case IN_USE -> newStatus == RoomStatus.AVAILABLE || newStatus == RoomStatus.MAINTENANCE;
			case MAINTENANCE -> newStatus == RoomStatus.AVAILABLE;
		};
	}
}

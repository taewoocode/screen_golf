package com.example.screen_golf.room.respository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;

public interface RoomRepository extends JpaRepository<Room, Long> {

	/**
	 * 주어진 roomType에 대해, 예약 시작 시간과 종료 시간 사이에 겹치는 예약이
	 * 없으며 상태가 AVAILABLE인 방을 검색합니다.
	 *
	 * JPQL에서 서브쿼리를 사용하여, 해당 방(r)에 대해,
	 * 이미 존재하는 예약(Reservation)이 주어진 시간 범위와 겹치면 제외합니다.
	 *
	 * @param roomType 예약할 방 타입 (예: STANDARD, PREMIUM, VIP)
	 * @param startTime 예약 시작 시간
	 * @param endTime 예약 종료 시간
	 * @return 사용 가능한 방이 있다면 Optional로 반환
	 */
	@Query("SELECT r FROM Room r " +
		"WHERE r.roomType = :roomType " +
		"  AND r.status = 'AVAILABLE' " +
		"  AND NOT EXISTS (" +
		"        SELECT res FROM Reservation res " +
		"        WHERE res.room = r " +
		"          AND res.startTime < :endTime " +
		"          AND res.endTime > :startTime" +
		"  )")
	List<Room> findAvailableRoomByType(
		@Param("roomType") RoomType roomType,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);

	/**
	 * Type 필터회
	 * @param roomType
	 * @return
	 */
	List<Room> findByRoomType(RoomType roomType);

}

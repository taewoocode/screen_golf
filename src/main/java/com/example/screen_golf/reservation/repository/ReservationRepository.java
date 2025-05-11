package com.example.screen_golf.reservation.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	/**
	 * 특정 방에 대해 지정한 시간대에 이미 예약된 예약이 있는지 확인
	 * 조건:
	 * - 해당 방(Room)에서,
	 * - 예약 상태가 active (예: PENDING, CONFIRMED)인 예약,
	 * - 그리고 기존 예약의 시작 시간 < 새 예약 종료 시간, 기존 예약의 종료 시간 > 새 예약 시작 시간인 경우.
	 *
	 * @param room          대상 방
	 * @param startTime     새 예약의 시작 시간
	 * @param endTime       새 예약의 종료 시간
	 * @param activeStatuses 중복 체크에 사용할 예약 상태 목록
	 * @return 중복되는 예약 리스트
	 */
	@Query("SELECT r FROM Reservation r " +
		"WHERE r.room = :room " +
		"AND r.status IN :activeStatuses " +
		"AND (r.startTime < :endTime AND r.endTime > :startTime)")
	List<Reservation> findOverlappingReservations(
		@Param("room") Room room,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime,
		@Param("activeStatuses") List<ReservationStatus> activeStatuses);

	/**
	 * 지정한 운영시간 범위(예: 특정 날짜의 11:00 ~ 22:00) 내에 있으며,
	 * 특정 룸 타입에 해당하는 예약들을 조회
	 *
	 * @param operatingStart 운영 시작 시간 (LocalDateTime)
	 * @param operatingEnd   운영 종료 시간 (LocalDateTime)
	 * @param roomType       원하는 룸 타입 (STANDARD, PREMIUM, VIP 등)
	 * @return 조건에 해당하는 예약 리스트
	 */
	@Query("SELECT r FROM Reservation r " +
		"WHERE r.startTime >= :operatingStart " +
		"AND r.endTime <= :operatingEnd " +
		"AND r.room.roomType = :roomType")
	List<Reservation> findReservationsByOperatingHoursAndRoomType(
		@Param("operatingStart") LocalDateTime operatingStart,
		@Param("operatingEnd") LocalDateTime operatingEnd,
		@Param("roomType") RoomType roomType);

	/**
	 * 사용자 ID를 통해 해당 사용자의 예약 내역을 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 예약 리스트
	 */
	List<Reservation> findByUserId(Long userId);

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
	Optional<Room> findAvailableRoomByType(
		@Param("roomType") RoomType roomType,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);

	/**
	 * 특정 방의 특정 날짜에 대한 예약 목록을 조회합니다.
	 */
	@Query("SELECT r FROM Reservation r WHERE r.room = :room AND DATE(r.startTime) = :date")
	List<Reservation> findByRoomAndDate(@Param("room") Room room, @Param("date") LocalDate date);

	/**
	 * 특정 방의 특정 시간대에 예약이 있는지 확인합니다.
	 */
	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
		   "FROM Reservation r " +
		   "WHERE r.room = :room " +
		   "AND r.status IN :statuses " +
		   "AND r.startTime < :endTime " +
		   "AND r.endTime > :startTime")
	boolean existsByRoomAndTimeRange(
		@Param("room") Room room,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime,
		@Param("statuses") List<ReservationStatus> statuses
	);

	/**
	 * 특정 상태의 예약 목록을 조회합니다.
	 */
	List<Reservation> findByStatus(ReservationStatus status);

	/**
	 * 특정 방의 특정 시간대에 예약 가능한지 확인합니다.
	 */
	@Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END " +
		   "FROM Reservation r " +
		   "WHERE r.room = :room " +
		   "AND r.status IN :statuses " +
		   "AND r.startTime < :endTime " +
		   "AND r.endTime > :startTime")
	boolean isTimeSlotAvailable(
		@Param("room") Room room,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime,
		@Param("statuses") List<ReservationStatus> statuses
	);

	/**
	 * 결제가 필요한 예약 목록을 조회합니다.
	 */
	@Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.createdAt < :cutoffTime")
	List<Reservation> findPendingReservationsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);

	List<Reservation> findByRoomAndStartTimeBetween(Room room, LocalDateTime start, LocalDateTime end);
	Optional<Reservation> findById(Long id);

}

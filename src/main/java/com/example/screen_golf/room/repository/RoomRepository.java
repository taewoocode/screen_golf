package com.example.screen_golf.room.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
import com.example.screen_golf.room.domain.RoomType;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	List<Room> findByStatus(RoomStatus status);

	List<Room> findByRoomType(RoomType roomType);

	Optional<Room> findById(Long id);

	@Query("SELECT r FROM Room r " +
		"WHERE r.roomType = :roomType " +
		"AND r.status = 'AVAILABLE' " +
		"AND r.capacity >= :userCount " +
		"AND NOT EXISTS (" +
		"    SELECT res FROM Reservation res " +
		"    WHERE res.room = r " +
		"    AND res.startTime < :endDateTime " +
		"    AND res.endTime > :startDateTime" +
		")")
	List<Room> findAvailableRooms(
		@Param("startDateTime") LocalDateTime startDateTime,
		@Param("endDateTime") LocalDateTime endDateTime,
		@Param("userCount") int userCount,
		@Param("roomType") RoomType roomType
	);

}
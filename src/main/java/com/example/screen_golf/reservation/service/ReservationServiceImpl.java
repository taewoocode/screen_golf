package com.example.screen_golf.reservation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.screen_golf.exception.reservation.ReservationConflictException;
import com.example.screen_golf.exception.reservation.ResourceNotFoundException;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;
import com.example.screen_golf.room.respository.RoomRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;

	@Override
	public Reservation.ReservationResponse createReservation(Reservation.ReservationBookingRequest request) {
		request.validateOperatingHours();

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));

		LocalDateTime reservationStart = request.getReservationStartDateTime();
		LocalDateTime reservationEnd = request.getReservationEndDateTime();

		List<Room> availableRooms = roomRepository.findAvailableRoomByType(
			request.getRoomType(), reservationStart, reservationEnd);

		if (availableRooms.isEmpty()) {
			throw new ResourceNotFoundException("해당 타입의 예약 가능한 방이 없습니다.");
		}

		// EX - LIST.get(0) VIP SELECT
		Room selectedRoom = availableRooms.get(0);

		List<ReservationStatus> activeStatuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
		List<Reservation> overlappingReservations =
			reservationRepository.findOverlappingReservations(selectedRoom, reservationStart, reservationEnd,
				activeStatuses);
		if (!overlappingReservations.isEmpty()) {
			throw new ReservationConflictException("선택한 시간대에 이미 예약이 존재합니다.");
		}

		Reservation reservation = Reservation.builder()
			.user(user)
			.room(selectedRoom)
			.startTime(reservationStart)
			.endTime(reservationEnd)
			.memo(request.getMemo())
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);
		return Reservation.ReservationResponse.fromEntity(savedReservation);
	}

	@Override
	public List<Reservation.AvailableRoomResponse> searchAvailableRooms(Reservation.ReservationSearchRequest request) {
		//예약 조회
		request.validateOperatingHours();
		LocalDateTime reservationStartDateTime = request.getReservationStartDateTime();
		LocalDateTime reservationEndDateTime = request.getReservationEndDateTime();
		RoomType roomType = request.getRoomType();

		List<Reservation> reservationRoomType =
			reservationRepository.findReservationsByOperatingHoursAndRoomType(
				reservationStartDateTime, reservationEndDateTime, roomType);

		return reservationRoomType.stream()
			.map(r -> Reservation.AvailableRoomResponse.fromRoom(r.getRoom()))
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(Reservation.AvailableRoomResponse::getRoomId, Function.identity(), (a, b) -> a),
				map -> new ArrayList<>(map.values())
			));
	}

	@Override
	public List<Reservation.ReservationResponse> getUserReservations(Long userId) {
		List<Reservation> reservations = reservationRepository.findByUserId(userId);
		return reservations.stream()
			.map(Reservation.ReservationResponse::fromEntity)
			.collect(Collectors.toList());
	}
}
package com.example.screen_golf.reservation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
	public Reservation.ReservationResponse createReservation(Reservation.ReservationBookingRequest request) {
		request.validateOperatingHours();
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		Room room = roomRepository.findById(request.getRoomId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

		LocalDateTime reservationStartDateTime = request.getReservationStartDateTime();
		LocalDateTime reservationEndDateTime = request.getReservationEndDateTime();
		List<ReservationStatus> activeStatus
			= List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

		List<Reservation> overlappingResponse = reservationRepository.findOverlappingReservations(
			room, reservationStartDateTime, reservationEndDateTime, activeStatus);
		if (!overlappingResponse.isEmpty()) {
			throw new IllegalArgumentException("선택한 예약 시간대가 이미 존재합니다.");
		}

		Reservation reservationResponse = Reservation.builder()
			.user(user)
			.room(room)
			.startTime(reservationStartDateTime)
			.endTime(reservationEndDateTime)
			.memo(request.getMemo())
			.build();
		Reservation saveReservationResponse = reservationRepository.save(reservationResponse);
		return Reservation.ReservationResponse.fromEntity(saveReservationResponse);
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
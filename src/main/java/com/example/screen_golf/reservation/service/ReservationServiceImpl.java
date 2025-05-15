package com.example.screen_golf.reservation.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.common.service.DistributedLockService;
import com.example.screen_golf.exception.reservation.ReservationNotAvailableException;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.dto.ReservationConverter;
import com.example.screen_golf.reservation.dto.ReservationInfo;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.repository.RoomRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final ReservationConverter reservationConverter;
	private final DistributedLockService lockService;

	private static final LocalTime OPEN_TIME = LocalTime.of(9, 0);  // 오픈 시간: 09:00
	private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0); // 마감 시간: 22:00

	@Override
	@Transactional
	public ReservationInfo.ReservationResponse createReservation(ReservationInfo.ReservationRequest request) {
		log.info("Received reservation request: {}", request);

		String lockKey = String.format("reservation:%d:%s",
			request.getRoomId(),
			request.getStartTime().toString());

		try {
			if (!lockService.tryLock(lockKey, 3, 10)) {
				throw new ReservationNotAvailableException("다른 사용자가 예약 중입니다. 잠시 후 다시 시도해주세요.");
			}

			User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

			Room room = roomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

			Payment payment = null;
			if (request.getPaymentId() != null) {
				payment = paymentRepository.findById(request.getPaymentId())
					.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

				if (payment.getStatus() == PaymentStatus.COMPLETED) {
					Reservation reservation = Reservation.builder()
						.user(user)
						.room(room)
						.startTime(request.getStartTime())
						.endTime(request.getEndTime())
						.status(ReservationStatus.CONFIRMED)
						.payment(payment)
						.build();

					Reservation savedReservation = reservationRepository.save(reservation);
					log.info("예약이 성공적으로 생성되었습니다={}", savedReservation);
					return ReservationInfo.ReservationResponse.toDto(savedReservation);
				} else if (payment.getStatus() == PaymentStatus.FAILED) {
					throw new ReservationNotAvailableException("결제가 실패했습니다. 예약을 취소합니다.");
				} else {
					throw new ReservationNotAvailableException("결제가 완료되지 않았습니다.");
				}
			} else {
				Reservation reservation = Reservation.builder()
					.user(user)
					.room(room)
					.startTime(request.getStartTime())
					.endTime(request.getEndTime())
					.status(ReservationStatus.CONFIRMED)
					.build();

				Reservation savedReservation = reservationRepository.save(reservation);
				log.info("예약이 성공적으로 생성되었습니다={}", savedReservation);
				return ReservationInfo.ReservationResponse.toDto(savedReservation);
			}
		} catch (Exception e) {
			log.error("Failed to create reservation: {}", e.getMessage());
			throw e;
		} finally {
			lockService.unlock(lockKey);
		}
	}

	@Override
	public List<ReservationInfo.AvailableRoomResponse> getAvailableRooms(
		ReservationInfo.AvailableRoomsRequest request) {
		log.info("예약 가능한 방 조회 시작 - 날짜: {}, 방 타입: {}, 사용자 수: {}",
			request.getDate(), request.getRoomType(), request.getUserCount());

		LocalDateTime startDateTime = request.getDate().with(OPEN_TIME);
		LocalDateTime endDateTime = request.getDate().with(CLOSE_TIME);

		List<Room> availableRooms = roomRepository.findAvailableRooms(
			startDateTime,
			endDateTime,
			request.getUserCount(),
			request.getRoomType()
		);
		List<ReservationInfo.AvailableRoomResponse> response = availableRooms.stream()
			.map(reservationConverter::toAvailableRoomResponse)
			.collect(Collectors.toList());
		log.info("예약 가능한 방 조회 완료 - 조회된 방 수: {}", response.size());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReservationInfo.AvailableTimeSlotResponse> getAvailableTimeSlots(
		ReservationInfo.AvailableTimeSlotsRequest request) {
		LocalDateTime date = request.getDate();
		Long roomId = request.getRoomId();

		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

		List<Reservation> existingReservations = reservationRepository.findByRoomAndDate(room, date.toLocalDate());

		List<ReservationInfo.AvailableTimeSlotResponse> timeSlots = new ArrayList<>();
		LocalDateTime currentTime = date.with(OPEN_TIME);
		LocalDateTime endTime = date.with(CLOSE_TIME);

		while (currentTime.isBefore(endTime)) {
			LocalDateTime slotEndTime = currentTime.plusMinutes(30);
			boolean isAvailable = isTimeSlotAvailable(currentTime, slotEndTime, existingReservations);

			timeSlots.add(ReservationInfo.AvailableTimeSlotResponse.builder()
				.startTime(currentTime)
				.endTime(slotEndTime)
				.available(isAvailable)
				.build());

			currentTime = slotEndTime;
		}

		return timeSlots;
	}

	@Override
	@Transactional(readOnly = true)
	public ReservationInfo.ReservationResponse checkPaymentStatus(Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
		return null;
	}

	private boolean isTimeSlotAvailable(LocalDateTime startTime, LocalDateTime endTime,
		List<Reservation> existingReservations) {
		return existingReservations.stream()
			.noneMatch(reservation ->
				(startTime.isBefore(reservation.getEndTime()) &&
					endTime.isAfter(reservation.getStartTime())) &&
					reservation.getStatus() == ReservationStatus.CONFIRMED
			);
	}
}

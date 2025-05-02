package com.example.screen_golf.reservation.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.exception.reservation.ReservationNotAvailableException;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationHistory;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.dto.ReservationInfo;
import com.example.screen_golf.reservation.repository.ReservationHistoryRepository;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
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
	private final ReservationHistoryRepository reservationHistoryRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;

	private static final LocalTime OPEN_TIME = LocalTime.of(9, 0);  // 오픈 시간: 09:00
	private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0); // 마감 시간: 22:00

	@Override
	@Transactional(readOnly = true)
	public List<ReservationInfo.AvailableRoomResponse> getAvailableRooms(
		ReservationInfo.AvailableRoomsRequest request) {
		LocalDateTime date = request.getDate();

		List<Room> availableRooms = roomRepository.findByStatus(RoomStatus.AVAILABLE).stream()
			.filter(room -> request.getRoomType() == null || room.getRoomType() == request.getRoomType())
			.filter(room -> !isTimeSlotBooked(date.with(OPEN_TIME), date.with(CLOSE_TIME), room))
			.collect(Collectors.toList());

		return availableRooms.stream()
			.map(room -> ReservationInfo.AvailableRoomResponse.builder()
				.roomId(room.getId())
				.roomName(room.getName())
				.roomType(room.getRoomType())
				.pricePerHour(room.getPricePerHour())
				.description(room.getDescription())
				.build())
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReservationInfo.AvailableTimeSlotResponse> getAvailableTimeSlots(
		ReservationInfo.AvailableTimeSlotsRequest request) {
		LocalDateTime date = request.getDate();
		Long roomId = request.getRoomId();

		// 방 존재 여부 확인
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

		// 해당 날짜의 예약 목록 조회
		List<Reservation> existingReservations = reservationRepository.findByRoomAndDate(room, date.toLocalDate());

		// 30분 단위로 시간대 생성
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
	@Transactional
	public ReservationInfo.ReservationResponse createReservation(ReservationInfo.ReservationRequest request) {
		// 사용자 조회
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Room room = roomRepository.findById(request.getRoomId())
			.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

		validateTimeSlot(request.getStartTime(), request.getEndTime());

		if (isTimeSlotBooked(request.getStartTime(), request.getEndTime(), room)) {
			throw new ReservationNotAvailableException("이미 예약된 시간대입니다.");
		}

		// 임시 Reservation 객체 생성
		Reservation tempReservation = Reservation.builder()
			.user(user)
			.room(room)
			.startTime(request.getStartTime())
			.endTime(request.getEndTime())
			.build();

		// 결제 정보 조회
		List<Payment> payments = paymentRepository.findByReservation(tempReservation);
		Payment existingPayment = payments.isEmpty() ? null : payments.get(0);

		ReservationStatus initialStatus;
		if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.COMPLETED) {
			initialStatus = ReservationStatus.CONFIRMED;
		} else {
			initialStatus = ReservationStatus.PENDING;
		}

		Reservation reservation = Reservation.builder()
			.user(user)
			.room(room)
			.startTime(request.getStartTime())
			.endTime(request.getEndTime())
			.status(initialStatus)
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);

		ReservationHistory history = ReservationHistory.builder()
			.reservation(savedReservation)
			.previousStatus(null)
			.newStatus(initialStatus)
			.reason(initialStatus == ReservationStatus.CONFIRMED ? "결제 완료로 인한 예약 확정" : "예약 생성")
			.build();

		reservationHistoryRepository.save(history);

		return ReservationInfo.ReservationResponse.toDto(savedReservation);
	}

	@Override
	@Transactional(readOnly = true)
	public ReservationInfo.ReservationResponse checkPaymentStatus(Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

		List<Payment> payments = paymentRepository.findByReservation(reservation);
		Payment payment = payments.isEmpty() ? null : payments.get(0);

		if (payment == null || payment.getStatus() != PaymentStatus.COMPLETED) {
			return ReservationInfo.ReservationResponse.builder()
				.reservationId(reservation.getId())
				.roomId(reservation.getRoom().getId())
				.roomName(reservation.getRoom().getName())
				.startTime(reservation.getStartTime())
				.endTime(reservation.getEndTime())
				.status(ReservationStatus.PENDING)
				.totalAmount(reservation.calculateTotalAmount())
				.paymentRequired(true)
				.message("결제가 필요합니다. 결제를 진행해주세요.")
				.build();
		}

		// 결제가 완료된 경우
		return ReservationInfo.ReservationResponse.builder()
			.reservationId(reservation.getId())
			.roomId(reservation.getRoom().getId())
			.roomName(reservation.getRoom().getName())
			.startTime(reservation.getStartTime())
			.endTime(reservation.getEndTime())
			.status(ReservationStatus.CONFIRMED)
			.totalAmount(reservation.calculateTotalAmount())
			.paymentRequired(false)
			.message("예약이 완료되었습니다.")
			.build();
	}

	private void validateTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
		// 운영 시간 체크
		if (startTime.toLocalTime().isBefore(OPEN_TIME) ||
			endTime.toLocalTime().isAfter(CLOSE_TIME)) {
			throw new ReservationNotAvailableException("운영 시간이 아닙니다.");
		}

		// 최소 예약 시간 체크 (30분)
		if (startTime.plusMinutes(30).isAfter(endTime)) {
			throw new ReservationNotAvailableException("최소 예약 시간은 30분입니다.");
		}
	}

	private boolean isTimeSlotBooked(LocalDateTime startTime, LocalDateTime endTime, Room room) {
		return reservationRepository.existsByRoomAndTimeRange(
			room,
			startTime,
			endTime,
			List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
		);
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

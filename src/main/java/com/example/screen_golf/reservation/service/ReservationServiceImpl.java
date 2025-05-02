package com.example.screen_golf.reservation.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.exception.reservation.ReservationNotAvailableException;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.payment.service.PaymentService;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.dto.ReservationInfo;
import com.example.screen_golf.reservation.repository.ReservationHistoryRepository;
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
	private final ReservationHistoryRepository reservationHistoryRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final PaymentService paymentService;

	private static final LocalTime OPEN_TIME = LocalTime.of(9, 0);  // 오픈 시간: 09:00
	private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0); // 마감 시간: 22:00

	@Override
	@Transactional
	public ReservationInfo.ReservationResponse createReservation(ReservationInfo.ReservationRequest request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Room room = roomRepository.findById(request.getRoomId())
			.orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

		Payment payment = null;
		if (request.getPaymentId() != null) {
			payment = paymentRepository.findById(request.getPaymentId())
				.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

			if (payment.getStatus() != PaymentStatus.COMPLETED) {
				throw new IllegalArgumentException("결제가 완료되지 않았습니다.");
			}
		}

		// 결제 상태가 실패인 경우 예약을 취소
		if (payment != null && payment.getStatus() == PaymentStatus.FAILED) {
			throw new ReservationNotAvailableException("결제가 실패했습니다. 예약을 취소합니다.");
		}

		Reservation reservation = null;
		if (payment != null && payment.getStatus() == PaymentStatus.COMPLETED) {
			// 예약 생성
			reservation = Reservation.builder()
				.user(user)
				.room(room)
				.startTime(request.getStartTime())
				.endTime(request.getEndTime())
				.status(ReservationStatus.CONFIRMED)
				.payment(payment)
				.build();

			paymentService.approve(reservation);

			Reservation savedReservation = reservationRepository.save(reservation);
			return ReservationInfo.ReservationResponse.toDto(savedReservation);
		} else {
			// 결제 상태가 실패일 경우, 예약을 생성하지 않고 예외 처리
			throw new ReservationNotAvailableException("결제 실패로 예약을 생성할 수 없습니다.");
		}
	}

	@Override
	public List<ReservationInfo.AvailableRoomResponse> getAvailableRooms(
		ReservationInfo.AvailableRoomsRequest request) {
		return null;
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

	private String determineTimeSlot(LocalDateTime startTime) {
		LocalTime time = startTime.toLocalTime();
		if (time.isBefore(LocalTime.of(12, 0))) {
			return "09:00-12:00";
		} else if (time.isBefore(LocalTime.of(18, 0))) {
			return "12:00-18:00";
		} else {
			return "18:00-22:00";
		}
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

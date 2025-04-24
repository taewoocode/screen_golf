package com.example.screen_golf.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.domain.Room;
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
	public Reservation.ReservationResponse createReservation(Reservation.ReservationRequest request) {
		try {
			log.info("예약 생성 시작 - 룸 ID: {}, 시작 시간: {}", request.getRoomId(), request.getStartTime());

			// 1. 사용자와 룸 조회
			User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
			Room room = roomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 룸입니다."));

			// 2. 예약 객체 생성
			Reservation reservation = Reservation.builder()
				.user(user)
				.room(room)
				.startTime(request.getStartTime())
				.endTime(request.getEndTime())
				.memo(request.getMemo())
				.build();

			// 3. 예약 저장
			Reservation savedReservation = reservationRepository.save(reservation);
			log.info("예약 생성 완료 - 예약 ID: {}", savedReservation.getId());

			// 4. 응답 생성
			return Reservation.ReservationResponse.builder()
				.reservationId(savedReservation.getId())
				.userId(savedReservation.getUser().getId())
				.roomId(savedReservation.getRoom().getId())
				.startTime(savedReservation.getStartTime())
				.endTime(savedReservation.getEndTime())
				.status(savedReservation.getStatus())
				.memo(savedReservation.getMemo())
				.createdAt(savedReservation.getCreatedAt())
				.updatedAt(savedReservation.getUpdatedAt())
				.build();
		} catch (IllegalArgumentException e) {
			log.error("예약 생성 실패 (유효성 검사) - 룸 ID: {}", request.getRoomId(), e);
			throw e;
		} catch (Exception e) {
			log.error("예약 생성 실패 (서버 오류) - 룸 ID: {}", request.getRoomId(), e);
			throw new RuntimeException("예약 생성 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 *
	 * @param reservationId 예약 ID
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Reservation.ReservationResponse getReservation(Long reservationId) {
		try {
			log.info("예약 조회 시작 - 예약 ID: {}", reservationId);

			// 1. 예약 조회
			Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

			// 2. 응답 생성
			Reservation.ReservationResponse response = Reservation.ReservationResponse.builder()
				.reservationId(reservation.getId())
				.userId(reservation.getUser().getId())
				.roomId(reservation.getRoom().getId())
				.startTime(reservation.getStartTime())
				.endTime(reservation.getEndTime())
				.status(reservation.getStatus())
				.memo(reservation.getMemo())
				.createdAt(reservation.getCreatedAt())
				.updatedAt(reservation.getUpdatedAt())
				.build();

			log.info("예약 조회 완료 - 예약 ID: {}", response.getReservationId());
			return response;
		} catch (IllegalArgumentException e) {
			log.error("예약 조회 실패 (유효성 검사) - 예약 ID: {}", reservationId, e);
			throw e;
		} catch (Exception e) {
			log.error("예약 조회 실패 (서버 오류) - 예약 ID: {}", reservationId, e);
			throw new RuntimeException("예약 조회 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	@Transactional
	public void cancelReservation(Long reservationId) {
		try {
			log.info("예약 취소 시작 - 예약 ID: {}", reservationId);

			// 1. 예약 조회
			Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

			// 2. 예약 상태 변경
			reservation.changeStatus(ReservationStatus.CANCELLED);
			log.info("예약 취소 완료 - 예약 ID: {}", reservationId);
		} catch (IllegalArgumentException e) {
			log.error("예약 취소 실패 (유효성 검사) - 예약 ID: {}", reservationId, e);
			throw e;
		} catch (Exception e) {
			log.error("예약 취소 실패 (서버 오류) - 예약 ID: {}", reservationId, e);
			throw new RuntimeException("예약 취소 중 오류가 발생했습니다.", e);
		}
	}
} 
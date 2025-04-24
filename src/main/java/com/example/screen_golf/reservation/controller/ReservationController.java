package com.example.screen_golf.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.service.ReservationService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	/**
	 * 예약 진행
	 * 예약 가능한 방을 선택 후 예약 진행 요청 전송
	 * 운영시간 검증, 중복 체크 등을 거쳐 예약을 생성
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_RESERVATION_CREATE,
		description = SwaggerDocs.DESCRIPTION_RESERVATION_CREATE
	)
	public ResponseEntity<Reservation.ReservationResponse> createReservation(
		@Parameter(description = "예약 진행 요청 DTO", required = true)
		@RequestBody Reservation.ReservationBookingRequest request) {
		Reservation.ReservationResponse reservation = reservationService.createReservation(request);
		log.info("예약 성공 - 예약 ID ={}", reservation);
		return ResponseEntity.ok(reservation);
	}

	/**
	 * 예약 가능한 방 검색
	 * 원하는 날짜, 시간대, 룸 타입 조건에 맞춰 예약 가능한 방을 조회
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_RESERVATION_SEARCH_AVAILABLE,
		description = SwaggerDocs.DESCRIPTION_RESERVATION_SEARCH_AVAILABLE
	)
	@PostMapping("/available")
	public ResponseEntity<List<Reservation.AvailableRoomResponse>> searchAvailableRooms(
		@Parameter(description = "예약 가능한 방 검색 요청 DTO", required = true)
		@RequestBody Reservation.ReservationSearchRequest request) {
		List<Reservation.AvailableRoomResponse> response = reservationService.searchAvailableRooms(request);
		log.info("예약 가능한 방 검색 성공 - 조건: 날짜={}, 시작시간={}, 종료시간={}, 룸 타입={}",
			request.getDate(), request.getDesiredStartTime(), request.getDesiredEndTime(), request.getRoomType());
		return ResponseEntity.ok(response);
	}

	/**
	 * 사용자 별 예약 내역 조회
	 * 특정 사용자 ID를 기반으로 해당 사용자의 예약 내역을 조회합니다.
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_RESERVATION_GET_USER_RESERVATIONS,
		description = SwaggerDocs.DESCRIPTION_RESERVATION_GET_USER_RESERVATIONS
	)
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Reservation.ReservationResponse>> getUserReservations(
		@Parameter(description = "조회할 사용자 ID", required = true)
		@PathVariable("userId") Long userId) {
		List<Reservation.ReservationResponse> responses = reservationService.getUserReservations(userId);
		log.info("사용자 예약 내역 조회 성공 - 사용자 ID: {}", userId);
		return ResponseEntity.ok(responses);
	}
}

package com.example.screen_golf.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.reservation.dto.ReservationAvailableInfo;
import com.example.screen_golf.reservation.dto.ReservationCreateInfo;
import com.example.screen_golf.reservation.dto.ReservationSearchIdInfo;
import com.example.screen_golf.reservation.service.ReservationService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/reservations")
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
	@PostMapping
	public ResponseEntity<ReservationCreateInfo.ReservationCreateResponse> createReservation(
		@Parameter(description = "예약 진행 요청 DTO", required = true)
		@RequestBody ReservationCreateInfo.ReservationCreateRequest request) {
		ReservationCreateInfo.ReservationCreateResponse reservation = reservationService.createReservation(request);
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
	public ResponseEntity<List<ReservationAvailableInfo.ReservationAvaliableSearchResponse>> searchAvailableRooms(
		@Parameter(description = "예약 가능한 방 검색 요청 DTO", required = true)
		@RequestBody ReservationAvailableInfo.ReservationAvailableSearchRequest request) {
		List<ReservationAvailableInfo.ReservationAvaliableSearchResponse> reservationAvaliableSearchResponses
			= reservationService.searchAvailableRooms(
			request);
		log.info("예약 가능한 방 검색 성공 - 조건: 날짜={}, 시작시간={}, 종료시간={}, 룸 타입={},남은 방 갯수={}",
			request.getDate(), request.getDesiredStartTime(), request.getDesiredEndTime(), request.getRoomType(),
			reservationAvaliableSearchResponses.size());
		return ResponseEntity.ok(reservationAvaliableSearchResponses);
	}

	/**
	 * 사용자 별 예약 내역 조회
	 * 특정 사용자 ID를 기반으로 해당 사용자의 예약 내역을 조회
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_RESERVATION_GET_USER_RESERVATIONS,
		description = SwaggerDocs.DESCRIPTION_RESERVATION_GET_USER_RESERVATIONS
	)
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReservationSearchIdInfo.ReservationSearchIdResponse>> getUserReservations(
		@Parameter(description = "조회할 사용자 ID", required = true)
		@PathVariable("userId") ReservationSearchIdInfo.ReservationSearchIdRequest request) {
		List<ReservationSearchIdInfo.ReservationSearchIdResponse> responses = reservationService.getUserReservations(
			request);
		log.info("사용자 예약 내역 조회 성공 - 사용자 ID: {}", request);
		return ResponseEntity.ok(responses);
	}
}

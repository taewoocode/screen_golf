package com.example.screen_golf.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.jwts.CustomUserDetails;
import com.example.screen_golf.reservation.dto.ReservationInfo;
import com.example.screen_golf.reservation.service.ReservationService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation", description = "예약 관련 API")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@Operation(
		summary = "예약 가능한 방 목록 조회",
		description = "특정 날짜에 예약 가능한 방 목록을 조회합니다."
	)
	@GetMapping("/available-rooms")
	public ResponseEntity<List<ReservationInfo.AvailableRoomResponse>> getAvailableRooms(
		@RequestBody ReservationInfo.AvailableRoomsRequest request
	) {
		log.info("예약 가능한 방 조회 - 날짜: {}, 방 타입: {}", 
			request.getDate(), request.getRoomType());
		
		List<ReservationInfo.AvailableRoomResponse> response = 
			reservationService.getAvailableRooms(request);
		
		log.info("예약 가능한 방 조회 완료 - 조회된 방 수: {}", response.size());
		
		return ResponseEntity.ok(response);
	}

	@Operation(
		summary = "예약 가능한 시간대 조회",
		description = "특정 방의 예약 가능한 시간대를 조회합니다."
	)
	@GetMapping("/{roomId}/available-times")
	public ResponseEntity<List<ReservationInfo.AvailableTimeSlotResponse>> getAvailableTimeSlots(
		@PathVariable Long roomId,
		@RequestBody ReservationInfo.AvailableTimeSlotsRequest request
	) {
		log.info("예약 가능한 시간대 조회 - 방 ID: {}, 날짜: {}", 
			roomId, request.getDate());
		
		List<ReservationInfo.AvailableTimeSlotResponse> response = 
			reservationService.getAvailableTimeSlots(request);
		
		log.info("예약 가능한 시간대 조회 완료 - 조회된 시간대 수: {}", response.size());
		
		return ResponseEntity.ok(response);
	}

	@Operation(
		summary = "예약 생성",
		description = "새로운 예약을 생성합니다."
	)
	@PostMapping
	public ResponseEntity<ReservationInfo.ReservationResponse> createReservation(
		@RequestBody ReservationInfo.ReservationRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("예약 생성 요청 - 방 ID: {}, 시작 시간: {}, 종료 시간: {}", 
			request.getRoomId(), request.getStartTime(), request.getEndTime());
		
		ReservationInfo.ReservationResponse response = 
			reservationService.createReservation(request);
		
		log.info("예약 생성 완료 - 예약 ID: {}, 상태: {}", 
			response.getReservationId(), response.getStatus());
		
		return ResponseEntity.ok(response);
	}

	@Operation(
		summary = "결제 상태 확인",
		description = "예약의 결제 상태를 확인합니다."
	)
	@GetMapping("/{reservationId}/payment-status")
	public ResponseEntity<ReservationInfo.ReservationResponse> checkPaymentStatus(
		@PathVariable Long reservationId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("결제 상태 확인 - 예약 ID: {}", reservationId);
		
		ReservationInfo.ReservationResponse response = 
			reservationService.checkPaymentStatus(reservationId);
		
		log.info("결제 상태 확인 완료 - 예약 ID: {}, 상태: {}, 결제 필요: {}", 
			response.getReservationId(), response.getStatus(), response.isPaymentRequired());
		
		return ResponseEntity.ok(response);
	}
}

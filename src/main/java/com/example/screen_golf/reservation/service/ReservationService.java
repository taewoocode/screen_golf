package com.example.screen_golf.reservation.service;

import java.util.List;

import com.example.screen_golf.reservation.dto.ReservationInfo;
import com.example.screen_golf.reservation.domain.Reservation;

public interface ReservationService {
	/**
	 * 예약 가능한 방 목록을 조회합니다.
	 * @param request 조회 조건 (날짜, 방 타입)
	 * @return 예약 가능한 방 목록
	 */
	List<ReservationInfo.AvailableRoomResponse> getAvailableRooms(ReservationInfo.AvailableRoomsRequest request);

	/**
	 * 특정 방의 예약 가능한 시간대를 조회합니다.
	 * @param request 조회 조건 (방 ID, 날짜)
	 * @return 예약 가능한 시간대 목록
	 */
	List<ReservationInfo.AvailableTimeSlotResponse> getAvailableTimeSlots(ReservationInfo.AvailableTimeSlotsRequest request);

	/**
	 * 새로운 예약을 생성합니다.
	 * @param request 예약 정보 (방 ID, 시작 시간, 종료 시간)
	 * @return 생성된 예약 정보
	 */
	ReservationInfo.ReservationResponse createReservation(ReservationInfo.ReservationRequest request);

	/**
	 * 예약의 결제 상태를 확인합니다.
	 * @param reservationId 예약 ID
	 * @return 예약 정보 (결제 필요 여부 포함)
	 */
	ReservationInfo.ReservationResponse checkPaymentStatus(Long reservationId);
}

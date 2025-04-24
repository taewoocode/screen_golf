package com.example.screen_golf.reservation.service;

import com.example.screen_golf.reservation.domain.Reservation;

public interface ReservationService {
    /**
     * 예약 생성
     * @param request 예약 요청 정보
     * @return 생성된 예약 정보
     */
    Reservation.ReservationResponse createReservation(Reservation.ReservationRequest request);

    /**
     * 예약 조회
     * @param reservationId 예약 ID
     * @return 예약 정보
     */
    Reservation.ReservationResponse getReservation(Long reservationId);

    /**
     * 예약 취소
     * @param reservationId 예약 ID
     */
    void cancelReservation(Long reservationId);
} 
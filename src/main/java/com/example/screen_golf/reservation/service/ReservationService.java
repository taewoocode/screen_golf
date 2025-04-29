package com.example.screen_golf.reservation.service;

import java.util.List;

import com.example.screen_golf.reservation.dto.ReservationAvailableInfo;
import com.example.screen_golf.reservation.dto.ReservationCreateInfo;
import com.example.screen_golf.reservation.dto.ReservationSearchIdInfo;

public interface ReservationService {

	/**
	 * 예약 진행 요청 처리.
	 * 예약 진행 전, 운영시간 및 중복 예약 검증을 진행한 후 예약을 생성합니다.
	 *
	 * @param request 예약 진행 요청 DTO
	 * @return 생성된 예약의 결과 응답 DTO
	 */
	ReservationCreateInfo.ReservationCreateResponse createReservation(
		ReservationCreateInfo.ReservationCreateRequest request);

	/**
	 * 예약 가능한 방 검색 요청 처리.
	 * 지정한 날짜, 시간, 룸 타입에 맞춰 운영시간 내 예약 정보를 조회하여
	 * 해당 조건에 부합하는 방의 상세 정보를 반환합니다.
	 *
	 * @param request 예약 검색 요청 DTO
	 * @return 검색 결과로 얻은 예약 가능한 방 리스트
	 */
	List<ReservationAvailableInfo.ReservationAvaliableSearchResponse> searchAvailableRooms(
		ReservationAvailableInfo.ReservationAvailableSearchRequest request);

	/**
	 * ID로 사용자를 조회하는 DTO
	 * @param request
	 * @return
	 */
	List<ReservationSearchIdInfo.ReservationSearchIdResponse> getUserReservations(
		ReservationSearchIdInfo.ReservationSearchIdRequest request);
}

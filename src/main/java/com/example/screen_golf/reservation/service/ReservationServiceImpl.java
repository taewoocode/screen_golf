package com.example.screen_golf.reservation.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.exception.reservation.ReservationConflictException;
import com.example.screen_golf.exception.reservation.ResourceNotFoundException;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.dto.ReservationAvailableInfo;
import com.example.screen_golf.reservation.dto.ReservationCreateInfo;
import com.example.screen_golf.reservation.dto.ReservationSearchIdInfo;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;
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
	@Qualifier("rateAmountCountPolicy")
	private final DiscountPolicy discountPolicy;

	@Override
	@Transactional
	public ReservationCreateInfo.ReservationCreateResponse createReservation(
		ReservationCreateInfo.ReservationCreateRequest request) {
		// 운영 시간 검증 11시 ~ 22시
		request.validateOperatingHours();

		// User 검증
		User user = validateUser(request);

		LocalDateTime reservationStart = request.getReservationStartDateTime();
		LocalDateTime reservationEnd = request.getReservationEndDateTime();

		// 이용가능 한 Room 찾기
		List<Room> availableRooms = roomRepository.findAvailableRoomByType(
			request.getRoomType(), reservationStart, reservationEnd);

		// 룸을 이용가능한지 검증
		validateCheckRoomType(availableRooms);

		// 이용가능하면 룸 선택
		Room selectedRoom = availableRooms.get(0);

		// 룸 선택 시 중복된 시간이 있는지 확인
		validateRoomTimeCheck(selectedRoom, reservationStart, reservationEnd);

		// 없으면 쿠폰 적용
		applyCoupon(user, reservationStart, reservationEnd);
		return makeReservation(request, user, selectedRoom, reservationStart, reservationEnd);
	}

	@Override
	public List<ReservationAvailableInfo.ReservationAvaliableSearchResponse> searchAvailableRooms(
		ReservationAvailableInfo.ReservationAvailableSearchRequest request) {
		//예약 조회
		request.validateOperatingHours();
		LocalDateTime reservationStartDateTime = request.getReservationStartDateTime();
		LocalDateTime reservationEndDateTime = request.getReservationEndDateTime();
		RoomType roomType = request.getRoomType();

		List<Reservation> reservationRoomType =
			reservationRepository.findReservationsByOperatingHoursAndRoomType(
				reservationStartDateTime, reservationEndDateTime, roomType);

		return reservationRoomType.stream()
			.map(r -> ReservationAvailableInfo.ReservationAvaliableSearchResponse.fromRoom(r.getRoom()))
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(ReservationAvailableInfo.ReservationAvaliableSearchResponse::getRoomId,
					Function.identity(), (a, b) -> a),
				map -> new ArrayList<>(map.values())
			));
	}

	@Override
	public List<ReservationSearchIdInfo.ReservationSearchIdResponse> getUserReservations(
		ReservationSearchIdInfo.ReservationSearchIdRequest request) {
		List<Reservation> reservations = reservationRepository.findByUserId(request.getUserId());
		return reservations.stream()
			.map(ReservationSearchIdInfo.ReservationSearchIdResponse::convertReservationDto)
			.collect(Collectors.toList());
	}

	private static void validateCheckRoomType(List<Room> availableRooms) {
		if (availableRooms.isEmpty()) {
			throw new ResourceNotFoundException("해당 타입의 예약 가능한 방이 없습니다.");
		}
	}

	private User validateUser(ReservationCreateInfo.ReservationCreateRequest request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
		return user;
	}

	private ReservationCreateInfo.ReservationCreateResponse makeReservation(
		ReservationCreateInfo.ReservationCreateRequest request,
		User user, Room selectedRoom, LocalDateTime reservationStart, LocalDateTime reservationEnd) {
		Reservation reservation = Reservation.builder()
			.user(user)
			.room(selectedRoom)
			.startTime(reservationStart)
			.endTime(reservationEnd)
			.memo(request.getMemo())
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);
		return ReservationCreateInfo.ReservationCreateResponse.fromEntity(savedReservation);
	}

	private void applyCoupon(User user, LocalDateTime reservationStart, LocalDateTime reservationEnd) {
		if (user.getUserCoupons() != null) {
			for (UserCoupon userCoupon : user.getUserCoupons()) {
				if (userCoupon.isValid()) {
					applyCountDisCount(reservationStart, reservationEnd, userCoupon);
					break;
				}
			}
		}
	}

	private void validateRoomTimeCheck(Room selectedRoom, LocalDateTime reservationStart,
		LocalDateTime reservationEnd) {
		// 예약 시간 충돌 체크
		List<ReservationStatus> activeStatuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
		List<Reservation> overlappingReservations =
			reservationRepository.findOverlappingReservations(selectedRoom, reservationStart, reservationEnd,
				activeStatuses);
		if (!overlappingReservations.isEmpty()) {
			throw new ReservationConflictException("선택한 시간대에 이미 예약이 존재합니다.");
		}
	}

	private void applyCountDisCount(LocalDateTime reservationStart, LocalDateTime reservationEnd,
		UserCoupon userCoupon) {
		BigDecimal originalPrice = calculateOriginalPrice(reservationStart, reservationEnd);

		// 동적으로 할당
		BigDecimal discountAmount = discountPolicy.applyDiscount(originalPrice, userCoupon);
		BigDecimal finalPrice = originalPrice.subtract(discountAmount);
		finalPrice = finalPrice.max(BigDecimal.ZERO);

		log.info("할인 전 가격: " + originalPrice);
		log.info("할인 금액: " + discountAmount);
		log.info("할인 후 가격: " + finalPrice);
	}

	private BigDecimal calculateOriginalPrice(LocalDateTime reservationStart, LocalDateTime reservationEnd) {
		validateCheckTime(reservationStart, reservationEnd);
		long durationInHours = java.time.Duration.between(reservationStart, reservationEnd).toHours();
		BigDecimal hourlyRate = BigDecimal.valueOf(10000);
		BigDecimal originalPrice = hourlyRate.multiply(BigDecimal.valueOf(durationInHours));
		return originalPrice;
	}

	private static void validateCheckTime(LocalDateTime reservationStart, LocalDateTime reservationEnd) {
		if (reservationStart.isAfter(reservationEnd)) {
			throw new IllegalArgumentException("예약 종료 시간이 시작 시간보다 이전입니다.");
		}
	}
}
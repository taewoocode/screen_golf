package com.example.screen_golf.reservation.service;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.room.domain.RoomType;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;

@SpringBootTest
class ReservationServiceImplTest {

	@Mock
	private DiscountPolicy discountPolicy; // DiscountPolicy Mock

	@InjectMocks
	private ReservationServiceImpl reservationService; // ReservationServiceImpl Mock

	// UserCoupon 생성 메서드
	public UserCoupon createUserCoupon(User user, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return new UserCoupon(user, "TESTCODE", "KAKAO", 10000, startDateTime, endDateTime);
	}

	@Test
	void 쿠폰_적용_테스트() throws Exception {
		LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 26, 10, 0); // 예시로 2025년 4월 26일 10시
		LocalDateTime endDateTime = startDateTime.plusHours(1); // 1시간 후

		User user = new User(
			"test", "password", "testUser", "010-1234-5678", UserRole.USER, UserStatus.ACTIVE, "test");
		UserCoupon coupon = new UserCoupon(user, "TESTCODE", "KAKAO", 10000, startDateTime, endDateTime);
		user.getUserCoupons().add(coupon);

		Reservation.ReservationBookingRequest request = Reservation.ReservationBookingRequest.builder()
			.userId(user.getId())
			.date(LocalDate.now().plusDays(1))
			.startTime(LocalTime.of(10, 0))
			.endTime(LocalTime.of(11, 0))
			.roomType(RoomType.STANDARD)  // 예시로 룸 타입 추가
			.memo("Test memo")  // 메모 추가
			.build();
		reservationService.createReservation(request);
		verify(discountPolicy, times(1)).applyDiscount(any(), any()); // discountPolicy의 applyDiscount 메서드가 1번 호출되었는지 검증
	}
}

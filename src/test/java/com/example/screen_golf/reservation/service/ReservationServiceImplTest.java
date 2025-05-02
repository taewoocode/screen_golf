package com.example.screen_golf.reservation.service;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.repository.RoomRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

	@Mock
	private DiscountPolicy discountPolicy; // DiscountPolicy Mock
	// private final DiscountPolicy discountPolicy2 = new RateAmountCountPolicy();
	@Mock
	private UserRepository userRepository; // 추가된 의존성

	@Mock
	private RoomRepository roomRepository; // 추가된 의존성

	@Mock
	private ReservationRepository reservationRepository; // 추가된 의존성

	@InjectMocks
	private ReservationServiceImpl reservationService; // 테스트 대상

	// UserCoupon 생성 메서드
	// public UserCoupon createUserCoupon(User user, LocalDateTime startDateTime, LocalDateTime endDateTime) {
	// 	return new UserCoupon(user, "TESTCODE", "KAKAO", 10000, startDateTime, endDateTime);
	// }

	// @Test
	// @DisplayName("예약 설정을 11시 이전으로 하면 테스트가 실패합니다.")
	// void 쿠폰_적용_테스트_예약시간_설정_11시() throws Exception {
	// 	LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 26, 10, 0);
	// 	LocalDateTime endDateTime = startDateTime.plusHours(1);
	//
	// 	User user = new User("test", "password", "testUser", "010-1234-5678",
	// 		UserRole.USER, UserStatus.ACTIVE, "test");
	// 	if (user.getUserCoupons() == null) {
	// 		Field field = User.class.getDeclaredField("userCoupons");
	// 		field.setAccessible(true);
	// 		field.set(user, new ArrayList<UserCoupon>());
	// 	}
	// 	UserCoupon coupon = createUserCoupon(user, startDateTime.minusDays(1), endDateTime.plusDays(1));
	// 	user.getUserCoupons().add(coupon);
	//
	// 	Room room = Room.builder()
	// 		.name(String.valueOf(RoomType.STANDARD))
	// 		.build();
	//
	// 	Field idField = Room.class.getDeclaredField("id");
	// 	idField.setAccessible(true);
	// 	idField.set(room, 1L);
	//
	// 	// ReservationBookingRequest 생성
	// 	// Reservation.ReservationBookingRequest request = Reservation.ReservationBookingRequest.builder()
	// 	// 	.userId(1L)
	// 	// 	.date(LocalDate.of(2025, 4, 26))
	// 	// 	.startTime(LocalTime.of(10, 0))
	// 	// 	.endTime(LocalTime.of(11, 0))
	// 	// 	.roomType(RoomType.STANDARD)
	// 	// 	.memo("Test memo")
	// 	// 	.build();
	//
	// 	when(userRepository.findById(1L)).thenReturn(Optional.of(user));
	// 	when(roomRepository.findAvailableRoomByType(eq(RoomType.STANDARD), any(LocalDateTime.class),
	// 		any(LocalDateTime.class)))
	// 		.thenReturn(List.of(room));
	// 	when(reservationRepository.findOverlappingReservations(any(Room.class), any(LocalDateTime.class),
	// 		any(LocalDateTime.class), any()))
	// 		.thenReturn(List.of());
	// 	when(reservationRepository.save(any(Reservation.class)))
	// 		.thenAnswer(invocation -> invocation.getArgument(0));
	// 	when(discountPolicy.applyDiscount(any(BigDecimal.class), any(UserCoupon.class)))
	// 		.thenReturn(BigDecimal.valueOf(5000));
	//
	// 	reservationService.createReservation(request);
	//
	// 	verify(discountPolicy, times(1)).applyDiscount(any(BigDecimal.class), any(UserCoupon.class));
	// }

	// @Test
	// @DisplayName("예약 설정을 11시 이후로 하면 테스트가 성공합니다.")
	// void 쿠폰_적용_테스트_예약시간_설정_11시_이후() throws Exception {
	// 	LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 26, 11, 15);
	// 	LocalDateTime endDateTime = startDateTime.plusHours(1);
	//
	// 	User user = new User("test", "password", "testUser", "010-1234-5678",
	// 		UserRole.USER, UserStatus.ACTIVE, "test");
	// 	if (user.getUserCoupons() == null) {
	// 		Field field = User.class.getDeclaredField("userCoupons");
	// 		field.setAccessible(true);
	// 		field.set(user, new ArrayList<UserCoupon>());
	// 	}
	// 	UserCoupon coupon = createUserCoupon(user, startDateTime.minusDays(1), endDateTime.plusDays(1));
	// 	user.getUserCoupons().add(coupon);
	//
	// 	Room room = Room.builder()
	// 		.name(String.valueOf(RoomType.STANDARD))
	// 		.build();
	//
	// 	Field idField = Room.class.getDeclaredField("id");
	// 	idField.setAccessible(true);
	// 	idField.set(room, 1L);
	//
	// 	// ReservationBookingRequest 생성
	// 	Reservation.ReservationBookingRequest request = Reservation.ReservationBookingRequest.builder()
	// 		.userId(1L)
	// 		.date(LocalDate.of(2025, 4, 26))
	// 		.startTime(LocalTime.of(11, 15))
	// 		.endTime(LocalTime.of(12, 15))
	// 		.roomType(RoomType.STANDARD)
	// 		.memo("Test memo")
	// 		.build();
	//
	// 	when(userRepository.findById(1L)).thenReturn(Optional.of(user));
	// 	when(roomRepository.findAvailableRoomByType(eq(RoomType.STANDARD), any(LocalDateTime.class),
	// 		any(LocalDateTime.class)))
	// 		.thenReturn(List.of(room));
	// 	when(reservationRepository.findOverlappingReservations(any(Room.class), any(LocalDateTime.class),
	// 		any(LocalDateTime.class), any()))
	// 		.thenReturn(List.of());
	// 	when(reservationRepository.save(any(Reservation.class)))
	// 		.thenAnswer(invocation -> invocation.getArgument(0));
	// 	when(discountPolicy.applyDiscount(any(BigDecimal.class), any(UserCoupon.class)))
	// 		.thenReturn(BigDecimal.valueOf(5000));
	//
	// 	reservationService.createReservation(request);
	//
	// 	verify(discountPolicy, times(1)).applyDiscount(any(BigDecimal.class), any(UserCoupon.class));
	// }

	@Test
	@DisplayName("비율할인_정책_적용_테스트")
	void rateDiscountPolicy_할인계산_테스트() throws NoSuchMethodException, InstantiationException,
		IllegalAccessException, InvocationTargetException {
		BigDecimal originalPrice = BigDecimal.valueOf(10000);

		Constructor<UserCoupon> constructor = UserCoupon.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		UserCoupon coupon = constructor.newInstance();

		//할인율 10퍼센트 적용
		ReflectionTestUtils.setField(coupon, "discountAmount", 10);

		// DiscountPolicy discountPolicy = new RateAmountCountPolicy();

		BigDecimal calculatedPrice = discountPolicy.applyDiscount(originalPrice, coupon);
		BigDecimal expectedPrice = BigDecimal.valueOf(9000.0);

		assertEquals(expectedPrice, calculatedPrice);
	}

	@Test
	void 유저가_유저쿠폰을_보유한다() throws Exception {
		User user = new User("test", "password", "testUser", "010-1234-5678",
			UserRole.USER, UserStatus.ACTIVE, "test");

		ReflectionTestUtils.setField(user, "userCoupons", new ArrayList<UserCoupon>());

		Constructor<UserCoupon> couponConstructor = UserCoupon.class.getDeclaredConstructor();
		couponConstructor.setAccessible(true);
		UserCoupon coupon = couponConstructor.newInstance();

		ReflectionTestUtils.setField(coupon, "discountAmount", 10);

		((ArrayList<UserCoupon>)ReflectionTestUtils.getField(user, "userCoupons")).add(coupon);

		// 검증: coupon 리스트가 비어있지 않고, 정확히 하나의 쿠폰이 존재해야 하며,
		// 쿠폰의 discountAmount 값은 10이어야 함.
		ArrayList<UserCoupon> coupons = (ArrayList<UserCoupon>)ReflectionTestUtils.getField(user, "userCoupons");
		assertFalse(coupons.isEmpty(), "User coupon 리스트는 비어있으면 안 됩니다.");
		assertEquals(1, coupons.size(), "User coupon 리스트는 정확히 하나의 쿠폰을 포함해야 합니다.");

		int discountAmount = (int)ReflectionTestUtils.getField(coupon, "discountAmount");
		assertEquals(10, discountAmount, "쿠폰의 discountAmount는 10이어야 합니다.");
	}
}

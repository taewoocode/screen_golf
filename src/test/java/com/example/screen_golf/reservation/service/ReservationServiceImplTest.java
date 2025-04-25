package com.example.screen_golf.reservation.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;
import com.example.screen_golf.room.respository.RoomRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

	@Mock
	private DiscountPolicy discountPolicy; // DiscountPolicy Mock

	@Mock
	private UserRepository userRepository; // 추가된 의존성

	@Mock
	private RoomRepository roomRepository; // 추가된 의존성

	@Mock
	private ReservationRepository reservationRepository; // 추가된 의존성

	@InjectMocks
	private ReservationServiceImpl reservationService; // 테스트 대상

	// UserCoupon 생성 메서드
	public UserCoupon createUserCoupon(User user, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return new UserCoupon(user, "TESTCODE", "KAKAO", 10000, startDateTime, endDateTime);
	}

	@Test
	@DisplayName("예약 설정을 11시 이전으로 하면 테스트가 실패합니다.")
	void 쿠폰_적용_테스트_예약시간_설정_11시() throws Exception {
		LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 26, 10, 0);
		LocalDateTime endDateTime = startDateTime.plusHours(1);

		User user = new User("test", "password", "testUser", "010-1234-5678",
			UserRole.USER, UserStatus.ACTIVE, "test");
		if (user.getUserCoupons() == null) {
			Field field = User.class.getDeclaredField("userCoupons");
			field.setAccessible(true);
			field.set(user, new ArrayList<UserCoupon>());
		}
		UserCoupon coupon = createUserCoupon(user, startDateTime.minusDays(1), endDateTime.plusDays(1));
		user.getUserCoupons().add(coupon);

		Room room = Room.builder()
			.name(String.valueOf(RoomType.STANDARD))
			.build();

		Field idField = Room.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(room, 1L);

		// ReservationBookingRequest 생성
		Reservation.ReservationBookingRequest request = Reservation.ReservationBookingRequest.builder()
			.userId(1L)
			.date(LocalDate.of(2025, 4, 26))
			.startTime(LocalTime.of(10, 0))
			.endTime(LocalTime.of(11, 0))
			.roomType(RoomType.STANDARD)
			.memo("Test memo")
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findAvailableRoomByType(eq(RoomType.STANDARD), any(LocalDateTime.class),
			any(LocalDateTime.class)))
			.thenReturn(List.of(room));
		when(reservationRepository.findOverlappingReservations(any(Room.class), any(LocalDateTime.class),
			any(LocalDateTime.class), any()))
			.thenReturn(List.of());
		when(reservationRepository.save(any(Reservation.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		when(discountPolicy.applyDiscount(any(BigDecimal.class), any(UserCoupon.class)))
			.thenReturn(BigDecimal.valueOf(5000));

		reservationService.createReservation(request);

		verify(discountPolicy, times(1)).applyDiscount(any(BigDecimal.class), any(UserCoupon.class));
	}

	@Test
	@DisplayName("예약 설정을 11시 이후로 하면 테스트가 성공합니다.")
	void 쿠폰_적용_테스트_예약시간_설정_11시_이후() throws Exception {
		LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 26, 11, 15);
		LocalDateTime endDateTime = startDateTime.plusHours(1);

		User user = new User("test", "password", "testUser", "010-1234-5678",
			UserRole.USER, UserStatus.ACTIVE, "test");
		if (user.getUserCoupons() == null) {
			Field field = User.class.getDeclaredField("userCoupons");
			field.setAccessible(true);
			field.set(user, new ArrayList<UserCoupon>());
		}
		UserCoupon coupon = createUserCoupon(user, startDateTime.minusDays(1), endDateTime.plusDays(1));
		user.getUserCoupons().add(coupon);

		Room room = Room.builder()
			.name(String.valueOf(RoomType.STANDARD))
			.build();

		Field idField = Room.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(room, 1L);

		// ReservationBookingRequest 생성
		Reservation.ReservationBookingRequest request = Reservation.ReservationBookingRequest.builder()
			.userId(1L)
			.date(LocalDate.of(2025, 4, 26))
			.startTime(LocalTime.of(11, 15))
			.endTime(LocalTime.of(12, 15))
			.roomType(RoomType.STANDARD)
			.memo("Test memo")
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(roomRepository.findAvailableRoomByType(eq(RoomType.STANDARD), any(LocalDateTime.class),
			any(LocalDateTime.class)))
			.thenReturn(List.of(room));
		when(reservationRepository.findOverlappingReservations(any(Room.class), any(LocalDateTime.class),
			any(LocalDateTime.class), any()))
			.thenReturn(List.of());
		when(reservationRepository.save(any(Reservation.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		when(discountPolicy.applyDiscount(any(BigDecimal.class), any(UserCoupon.class)))
			.thenReturn(BigDecimal.valueOf(5000));

		reservationService.createReservation(request);

		verify(discountPolicy, times(1)).applyDiscount(any(BigDecimal.class), any(UserCoupon.class));
	}

}

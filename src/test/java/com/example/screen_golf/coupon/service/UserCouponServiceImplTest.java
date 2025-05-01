package com.example.screen_golf.coupon.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.dto.UserCouponListInfo;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceImplTest {

	@Mock
	private UserCouponRepository userCouponRepository;

	@InjectMocks
	private UserCouponServiceImpl userCouponService;

	@Test
	@DisplayName("사용자가_쿠폰을_보유하고_있으면_쿠폰_목록을_반환해야_한다")
	void 사용자가_쿠폰을_보유하고_있으면_쿠폰_목록을_반환해야_한다() throws Exception {
		// Given
		User testUser = User.builder()
			.email("testEmail")
			.password("test")
			.name("testUser")
			.phone("test")
			.role(UserRole.USER)
			.status(UserStatus.ACTIVE)
			.build();

		UserCoupon testCoupon1 = UserCoupon.builder()
			.couponCode("testCoupon1")
			.name("testCouponName1")
			.validFrom(LocalDateTime.now())
			.validTo(LocalDateTime.now().plusDays(10))
			.user(testUser)  // Coupon의 user 필드 설정
			.build();

		UserCoupon testCoupon2 = UserCoupon.builder()
			.couponCode("testCoupon2")
			.name("testCouponName2")
			.validFrom(LocalDateTime.now())
			.validTo(LocalDateTime.now().plusDays(10))
			.user(testUser)  // Coupon의 user 필드 설정
			.build();

		testUser.getUserCoupons().add(testCoupon1);
		testUser.getUserCoupons().add(testCoupon2);

		// When
		List<UserCoupon> userCoupons = testUser.getUserCoupons();

		// Then
		assertThat(userCoupons)
			.isNotNull()
			.hasSize(2)
			.contains(testCoupon1, testCoupon2);
	}

	@Test
	@DisplayName("사용자의 사용 가능한 쿠폰 리스트를 반환한다.")
	void 사용_가능한_쿠폰_리스트_반환() throws Exception {
		Long userId = 1L;
		UserCouponListInfo.UserCouponListRequest request = new UserCouponListInfo.UserCouponListRequest(userId);

		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		UserCoupon coupon = new UserCoupon(
			null, "COUPON123", "Test Coupon", 100, yesterday, tomorrow);

		when(userCouponRepository.findAvailableCoupons(eq(userId), eq(CouponStatus.UNUSED), any()))
			.thenReturn(List.of(coupon));

		List<UserCouponListInfo.UserCouponListResponse> result = userCouponService.getUserCouponsByUserId(request);

		assertThat(result.get(0).getCouponCode()).isEqualTo("COUPON123");
		assertThat(result.get(0).getName()).isEqualTo("Test Coupon");
		assertThat(result.get(0).getDiscountAmount()).isEqualTo(100);
	}

}

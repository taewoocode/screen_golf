package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;

@SpringBootTest
class UserCouponServiceImplTest {

	@Autowired
	private UserCouponRepository userCouponRepository;

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
		Assertions.assertThat(userCoupons)
			.isNotNull()
			.hasSize(2)
			.contains(testCoupon1, testCoupon2);
	}
}

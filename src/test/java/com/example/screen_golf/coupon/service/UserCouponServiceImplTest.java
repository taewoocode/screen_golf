package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;

@SpringBootTest
class UserCouponServiceImplTest {

	@Test
	@DisplayName("사용자가_쿠폰을_보유하고_있으면_쿠폰_목록을_반환해야_한다")
	void 사용자가_쿠폰을_보유하고_있으면_쿠폰_목록을_반환해야_한다() throws Exception {
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
			.user(testUser)
			.build();

		UserCoupon testCoupon2 = UserCoupon.builder()
			.couponCode("testCoupon2")
			.name("testCouponName2")
			.validFrom(LocalDateTime.now())
			.validTo(LocalDateTime.now().plusDays(10))
			.user(testUser)
			.build();

		testUser.getUserCoupons().add(testCoupon1);
		testUser.getUserCoupons().add(testCoupon2);

		List<UserCoupon> userCoupons = testUser.getUserCoupons();

		Assertions.assertThat(userCoupons)
			.isNotNull()
			.hasSize(2)
			.contains(testCoupon1, testCoupon2);
	}

}
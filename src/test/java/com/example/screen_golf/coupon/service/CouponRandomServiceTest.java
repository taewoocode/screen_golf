package com.example.screen_golf.coupon.service;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

@SpringBootTest
@Transactional
class CouponRandomServiceTest {

	@Autowired
	private CouponRandomService couponRandomService;

	@Autowired
	private UserCouponRepository userCouponRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 모든_유저에게_랜덤_쿠폰을_발급한다() {
		// given
		User user1 = userRepository.save(User.builder().name("철수").build());
		User user2 = userRepository.save(User.builder().name("영희").build());

		// when
		couponRandomService.issueMonthlyCouponToAllUsers();

		// then
		List<UserCoupon> coupons = userCouponRepository.findAll();
		Assertions.assertThat(coupons).hasSize(2);
		for (UserCoupon coupon : coupons) {
			System.out.println("발급된 쿠폰: " + coupon.getCouponPolicy().name());
		}
	}
}

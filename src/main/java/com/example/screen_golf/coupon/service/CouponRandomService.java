package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.CouponPolicy;
import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponRandomService {
	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;

	private static final Random random = new Random();

	//매월 1일 쿠폰을 발급한다.
	@Scheduled(cron = "0 0 0 1 * ?")
	@Transactional
	public void issueMonthlyCouponToAllUsers() {
		List<User> users = userRepository.findAll();
		for (User user : users) {
			CouponPolicy randomPolicy = getRandomPolicy();
			UserCoupon userCoupon = createUserCoupon(user, randomPolicy);
			userCouponRepository.save(userCoupon);
		}
	}

	private UserCoupon createUserCoupon(User user, CouponPolicy randomPolicy) {
		LocalDateTime now = LocalDateTime.now();
		return UserCoupon.builder()
			.user(user)
			.couponCode(UUID.randomUUID().toString())
			.couponPolicy(randomPolicy)
			.validFrom(now)
			.validTo(now.plusDays(50))
			.build();
	}

	private CouponPolicy getRandomPolicy() {
		CouponPolicy[] policies = CouponPolicy.values();
		return policies[random.nextInt(policies.length)];
	}
}

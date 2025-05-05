package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.Coupon;
import com.example.screen_golf.coupon.domain.CouponPolicy;
import com.example.screen_golf.coupon.repository.CouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponRandomService {

	private final UserRepository userRepository;
	private final CouponRepository userCouponRepository;

	private static final Random random = new Random();
	private static final int COUPON_VALID_DAYS = 50;

	// 매월 1일 00:00에 실행
	@Scheduled(cron = "0 0 0 1 * ?")
	@Transactional
	public void issueMonthlyCouponToAllUsers() {
		List<User> users = userRepository.findAllByStatus(UserStatus.ACTIVE);
		YearMonth currentMonth = YearMonth.now();

		for (User user : users) {
			if (hasAlreadyIssuedCouponThisMonth(user, currentMonth)) {
				log.info("이미 발급됨 - userId: {}", user.getId());
				continue;
			}

			CouponPolicy randomPolicy = getRandomPolicy();
			Coupon userCoupon = createUserCoupon(user, randomPolicy);
			userCouponRepository.save(userCoupon);

			log.info("쿠폰 발급 완료 - userId: {}, policy: {}", user.getId(), randomPolicy.name());
		}
	}

	private boolean hasAlreadyIssuedCouponThisMonth(User user, YearMonth currentMonth) {
		LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
		return userCouponRepository.existsByUserAndCreatedAtBetween(user, startOfMonth, endOfMonth);
	}

	private Coupon createUserCoupon(User user, CouponPolicy policy) {
		LocalDateTime now = LocalDateTime.now();
		return Coupon.builder()
			.user(user)
			.couponCode(UUID.randomUUID().toString())
			.couponPolicy(policy)
			.validFrom(now)
			.validTo(now.plusDays(COUPON_VALID_DAYS))
			.build();
	}

	private CouponPolicy getRandomPolicy() {
		CouponPolicy[] policies = CouponPolicy.values();
		return policies[random.nextInt(policies.length)];
	}
}

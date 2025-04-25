package com.example.screen_golf.coupon.service;

import org.springframework.stereotype.Service;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponServiceImpl implements UserCouponService {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;

	/**
	 * 쿠폰생성
	 * @param request
	 * @return
	 */
	@Override
	public UserCoupon.UserCouponCreateResponse createCoupon(UserCoupon.UserCouponCreateRequest request) {
		User user = validateUser(request);
		UserCoupon userCoupon = makeUserEntity(request, user);
		UserCoupon savedCoupon = userCouponRepository.save(userCoupon);
		return getUserCouponCreateResponse(savedCoupon, user);
	}

	private static UserCoupon.UserCouponCreateResponse getUserCouponCreateResponse(UserCoupon savedCoupon, User user) {
		return UserCoupon.UserCouponCreateResponse.builder()
			.userCouponId(savedCoupon.getId())
			.userId(user.getId())
			.couponCode(savedCoupon.getCouponCode())
			.getCouponName(savedCoupon.getName())
			.discountAmount(savedCoupon.getDiscountAmount())
			.validFrom(savedCoupon.getValidFrom())
			.validTo(savedCoupon.getValidTo())
			.createdAt(savedCoupon.getCreatedAt())
			.build();
	}

	private static UserCoupon makeUserEntity(UserCoupon.UserCouponCreateRequest request, User user) {
		return UserCoupon.builder()
			.user(user)
			.couponCode(request.getCouponCode())
			.name(request.getName())
			.discountAmount(request.getDiscountAmount())
			.validFrom(request.getValidFrom())
			.validTo(request.getValidTo())
			.build();
	}

	private User validateUser(UserCoupon.UserCouponCreateRequest request) {
		return userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
	}
}

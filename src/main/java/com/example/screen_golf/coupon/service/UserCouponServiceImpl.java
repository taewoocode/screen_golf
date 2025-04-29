package com.example.screen_golf.coupon.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.dto.UserCouponCreateInfo;
import com.example.screen_golf.coupon.dto.UserCouponDeleteInfo;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coupon", description = "쿠폰 관련 API")
@RequestMapping("api/coupon")
public class UserCouponServiceImpl implements UserCouponService {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;

	/**
	 * 쿠폰생성
	 * @param request
	 * @return
	 */
	@Override
	public UserCouponCreateInfo.UserCouponCreateResponse createCoupon(
		UserCouponCreateInfo.UserCouponCreateRequest request) {
		User user = validateUser(request);
		UserCoupon userCoupon = makeUserEntity(request, user);
		UserCoupon savedCoupon = userCouponRepository.save(userCoupon);
		return getUserCouponCreateResponse(savedCoupon, user);
	}

	/**
	 * 쿠폰삭제 사용자의 ID를 Param으로 받아, 쿠폰을 삭제합니다.
	 * @param userCouponId
	 * @return
	 */
	@Override
	@Transactional
	public UserCouponDeleteInfo.UserCouponDeleteResponse deleteCoupon(
		Long userCouponId) {

		userCouponRepository.deleteById(userCouponId);

		return UserCouponDeleteInfo.UserCouponDeleteResponse.builder()
			.userCouponId(userCouponId)
			.success(true)
			.build();
	}

	private static UserCouponCreateInfo.UserCouponCreateResponse getUserCouponCreateResponse(UserCoupon savedCoupon,
		User user) {
		return UserCouponCreateInfo.UserCouponCreateResponse.builder()
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

	private static UserCoupon makeUserEntity(UserCouponCreateInfo.UserCouponCreateRequest request, User user) {
		return UserCoupon.builder()
			.user(user)
			.couponCode(request.getCouponCode())
			.name(request.getName())
			.discountAmount(request.getDiscountAmount())
			.validFrom(request.getValidFrom())
			.validTo(request.getValidTo())
			.build();
	}

	private User validateUser(UserCouponCreateInfo.UserCouponCreateRequest request) {
		return userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
	}
}

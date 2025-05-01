package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.dto.UserCouponCreateInfo;
import com.example.screen_golf.coupon.dto.UserCouponDeleteInfo;
import com.example.screen_golf.coupon.dto.UserCouponListInfo;
import com.example.screen_golf.coupon.dto.UserCouponSearchCouponIdInfo;
import com.example.screen_golf.coupon.dto.UserCouponSearchUserIdInfo;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.exception.coupon.CouponNotFoundException;
import com.example.screen_golf.exception.user.UserNotFoundException;
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

	/**
	 * 단일 쿠폰 조회 -> 쿠폰ID를 입력받아 해당쿠폰 정보를 확인한다.
	 * @param userCouponId
	 * @return
	 */
	@Override
	public UserCouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse findCoupon(Long userCouponId) {
		Optional<UserCoupon> findByCouponId = userCouponRepository.findById(userCouponId);

		if (findByCouponId.isEmpty()) {
			throw new CouponNotFoundException("쿠폰을 찾을 수 없습니다.");
		}

		UserCoupon userCoupon = findByCouponId.get();

		return UserCouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse.builder()
			.id(userCoupon.getId())
			.userId(userCoupon.getUser().getId())
			.couponCode(userCoupon.getCouponCode())
			.name(userCoupon.getName())
			.discountAmount(userCoupon.getDiscountAmount())
			.validFrom(userCoupon.getValidFrom())
			.validTo(userCoupon.getValidTo())
			.status(userCoupon.getStatus())
			.createdAt(userCoupon.getCreatedAt())
			.updatedAt(userCoupon.getUpdatedAt())
			.build();

	}

	@Override
	public UserCouponSearchUserIdInfo.UserCouponSearchCouponIdResponse findCouponInfoByUserId(Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
		UserCoupon coupon = userCouponRepository.findByUser(user)
			.orElseThrow(() -> new CouponNotFoundException("해당 쿠폰을 찾을 수 없습니다."));
		return UserCouponSearchUserIdInfo.UserCouponSearchCouponIdResponse.builder()
			.userId(user.getId())
			.id(coupon.getId())
			.couponCode(coupon.getCouponCode())
			.name(coupon.getName())
			.discountAmount(coupon.getDiscountAmount())
			.validFrom(coupon.getValidFrom())
			.validTo(coupon.getValidTo())
			.status(coupon.getStatus())
			.createdAt(coupon.getCreatedAt())
			.updatedAt(coupon.getUpdatedAt())
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserCouponListInfo.UserCouponListResponse> getUserCouponsByUserId(
		UserCouponListInfo.UserCouponListRequest request) {
		LocalDateTime now = LocalDateTime.now();
		CouponStatus unusedCoupon = CouponStatus.UNUSED;

		List<UserCoupon> availableCoupons =
			userCouponRepository.findAvailableCoupons(request.getUserId(), unusedCoupon, now);

		return availableCoupons.stream()
			.map(UserCouponListInfo.UserCouponListResponse::toDto)
			.collect(Collectors.toList());
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

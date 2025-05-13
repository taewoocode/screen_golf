package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.Coupon;
import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.dto.CouponDeleteInfo;
import com.example.screen_golf.coupon.dto.CouponListInfo;
import com.example.screen_golf.coupon.dto.CouponSearchCouponIdInfo;
import com.example.screen_golf.coupon.dto.CouponSearchUserIdInfo;
import com.example.screen_golf.coupon.repository.CouponRepository;
import com.example.screen_golf.exception.coupon.CouponNotFoundException;
import com.example.screen_golf.exception.user.UserNotFoundException;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

	private final CouponRepository userCouponRepository;
	private final UserRepository userRepository;

	/**
	 * 사용자 쿠폰 삭제 기능
	 * - 사용자의 쿠폰 ID를 기반으로 해당 쿠폰을 삭제
	 * - 쿠폰이 존재하지 않으면 CouponNotFoundException을 발생
	 * @param userCouponId 삭제할 쿠폰의 ID
	 * @return 삭제된 쿠폰의 ID와 성공 여부를 포함한 응답 객체
	 */
	@Override
	@Transactional
	public CouponDeleteInfo.UserCouponDeleteResponse deleteCoupon(Long userCouponId) {
		if (!userCouponRepository.existsById(userCouponId)) {
			throw new CouponNotFoundException("삭제할 쿠폰을 찾을 수 없습니다.");
		}

		userCouponRepository.deleteById(userCouponId);

		return CouponDeleteInfo.UserCouponDeleteResponse.builder()
			.userCouponId(userCouponId)
			.success(true)
			.build();
	}

	/**
	 * 쿠폰 ID로 해당 쿠폰의 정보를 조회하는 기능
	 * - 쿠폰 ID를 통해 해당 쿠폰을 찾고, 쿠폰의 상세 정보를 반환
	 * - 쿠폰이 존재하지 않으면 CouponNotFoundException을 발생
	 * @param userCouponId 조회할 쿠폰의 ID
	 * @return 쿠폰의 상세 정보를 담은 응답 객체
	 */
	@Override
	public CouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse findCoupon(Long userCouponId) {
		Coupon userCoupon = userCouponRepository.findById(userCouponId)
			.orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

		return CouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse.builder()
			.id(userCoupon.getId())
			.userId(userCoupon.getUser().getId())
			.couponCode(userCoupon.getCouponCode())
			.couponPolicy(userCoupon.getCouponPolicy())
			.validFrom(userCoupon.getValidFrom())
			.validTo(userCoupon.getValidTo())
			.status(userCoupon.getStatus())
			.createdAt(userCoupon.getCreatedAt())
			.updatedAt(userCoupon.getUpdatedAt())
			.build();
	}

	/**
	 * 사용자 ID로 해당 사용자의 쿠폰 정보를 조회하는 기능
	 * - 사용자 ID를 통해 해당 사용자가 보유한 쿠폰을 조회하고, 쿠폰의 상세 정보를 반환
	 * - 사용자가 존재하지 않으면 UserNotFoundException을 발생시키고, 쿠폰이 없으면 CouponNotFoundException을 발생
	 * @param userId 조회할 사용자의 ID
	 * @return 사용자의 쿠폰 정보를 담은 응답 객체
	 */
	@Override
	public CouponSearchUserIdInfo.UserCouponSearchCouponIdResponse findCouponInfoByUserId(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

		Coupon coupon = userCouponRepository.findByUser(user)
			.orElseThrow(() -> new CouponNotFoundException("해당 쿠폰을 찾을 수 없습니다."));

		return CouponSearchUserIdInfo.UserCouponSearchCouponIdResponse.toDto(
			user, coupon, coupon.getPolicy());
	}

	/**
	 * 사용자 ID를 통해 해당 사용자의 미사용 쿠폰 목록을 조회하는 기능
	 * - 사용자의 미사용 쿠폰들을 조회하여 리스트로 반환
	 * - 쿠폰의 상태가 UNUSED인 쿠폰만 반환하며, 현재 날짜를 기준으로 유효한 쿠폰들만 반환
	 * @param request 사용자의 ID와 요청 정보를 포함한 요청 객체
	 * @return 미사용 쿠폰 목록을 담은 응답 객체 리스트
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CouponListInfo.UserCouponListResponse> getUserCouponsByUserId(
		CouponListInfo.UserCouponListRequest request) {

		List<Coupon> availableCoupons = userCouponRepository.findAvailableCoupons(
			request.getUserId(),
			CouponStatus.UNUSED,
			LocalDateTime.now()
		);

		return availableCoupons.stream()
			.map(CouponListInfo.UserCouponListResponse::toDto)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Integer validateAndUseCoupon(Long couponId, Integer originalAmount) {
		Coupon coupon = userCouponRepository.findById(couponId)
			.orElseThrow(() -> new IllegalArgumentException("유효한 쿠폰을 찾을 수 없습니다."));

		if (!coupon.isValid()) {
			throw new IllegalArgumentException("유효하지 않은 쿠폰입니다.");
		}
		if (!coupon.isAvailable()) {
			throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
		}
		
		coupon.use();
		userCouponRepository.save(coupon);

		return coupon.getPolicy().calculateDiscount(originalAmount);
	}
}

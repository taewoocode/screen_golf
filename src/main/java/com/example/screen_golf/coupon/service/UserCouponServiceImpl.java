package com.example.screen_golf.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.domain.UserCoupon;
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
	 * 사용자 쿠폰 삭제 기능
	 * - 사용자의 쿠폰 ID를 기반으로 해당 쿠폰을 삭제
	 * - 쿠폰이 존재하지 않으면 CouponNotFoundException을 발생
	 * @param userCouponId 삭제할 쿠폰의 ID
	 * @return 삭제된 쿠폰의 ID와 성공 여부를 포함한 응답 객체
	 */
	@Override
	@Transactional
	public UserCouponDeleteInfo.UserCouponDeleteResponse deleteCoupon(Long userCouponId) {
		if (!userCouponRepository.existsById(userCouponId)) {
			throw new CouponNotFoundException("삭제할 쿠폰을 찾을 수 없습니다.");
		}

		userCouponRepository.deleteById(userCouponId);

		return UserCouponDeleteInfo.UserCouponDeleteResponse.builder()
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
	public UserCouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse findCoupon(Long userCouponId) {
		UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
			.orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

		return UserCouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse.builder()
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
	public UserCouponSearchUserIdInfo.UserCouponSearchCouponIdResponse findCouponInfoByUserId(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

		UserCoupon coupon = userCouponRepository.findByUser(user)
			.orElseThrow(() -> new CouponNotFoundException("해당 쿠폰을 찾을 수 없습니다."));

		return UserCouponSearchUserIdInfo.UserCouponSearchCouponIdResponse.toDto(
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
	public List<UserCouponListInfo.UserCouponListResponse> getUserCouponsByUserId(
		UserCouponListInfo.UserCouponListRequest request) {

		List<UserCoupon> availableCoupons = userCouponRepository.findAvailableCoupons(
			request.getUserId(),
			CouponStatus.UNUSED,
			LocalDateTime.now()
		);

		return availableCoupons.stream()
			.map(UserCouponListInfo.UserCouponListResponse::toDto)
			.collect(Collectors.toList());
	}
}

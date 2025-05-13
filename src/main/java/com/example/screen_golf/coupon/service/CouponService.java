package com.example.screen_golf.coupon.service;

import java.util.List;

import com.example.screen_golf.coupon.dto.CouponDeleteInfo;
import com.example.screen_golf.coupon.dto.CouponListInfo;
import com.example.screen_golf.coupon.dto.CouponSearchCouponIdInfo;
import com.example.screen_golf.coupon.dto.CouponSearchUserIdInfo;

public interface CouponService {
	/**
	 * 쿠폰삭제
	 * @param userCouponId
	 * @return
	 */
	CouponDeleteInfo.UserCouponDeleteResponse deleteCoupon(Long userCouponId);

	/**
	 * UserCouponId로 쿠폰의 정보를 조회
	 */
	CouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse findCoupon(Long userCouponId);

	/**
	 * UserId로 쿠폰 정보 조회
	 */
	CouponSearchUserIdInfo.UserCouponSearchCouponIdResponse findCouponInfoByUserId(Long userId);

	/**
	 * User의 Id를 통해 User의 쿠폰리스트들을 조회
	 * @param request
	 * @return
	 */
	List<CouponListInfo.UserCouponListResponse> getUserCouponsByUserId(
		CouponListInfo.UserCouponListRequest request);

	/**
	 * 쿠폰 유효성 검증 및 할인 금액 계산
	 * @param couponId 쿠폰 ID
	 * @param originalAmount 원래 금액
	 * @return 할인된 금액
	 */
	Integer validateAndUseCoupon(Long couponId, Integer originalAmount);
}

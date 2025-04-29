package com.example.screen_golf.coupon.service;

import com.example.screen_golf.coupon.dto.UserCouponCreateInfo;
import com.example.screen_golf.coupon.dto.UserCouponDeleteInfo;
import com.example.screen_golf.coupon.dto.UserCouponSearchCouponIdInfo;

public interface UserCouponService {

	/**
	 * 쿠폰생성
	 * @param request
	 * @return
	 */
	UserCouponCreateInfo.UserCouponCreateResponse createCoupon(
		UserCouponCreateInfo.UserCouponCreateRequest request);

	/**
	 * 쿠폰삭제
	 * @param userCouponId
	 * @return
	 */
	UserCouponDeleteInfo.UserCouponDeleteResponse deleteCoupon(
		Long userCouponId);

	/**
	 * UserCouponId로 쿠폰의 정보를 조회
	 */
	UserCouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse findCoupon(
		Long userCouponId);
}

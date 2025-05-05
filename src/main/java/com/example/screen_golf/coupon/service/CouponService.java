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
	CouponDeleteInfo.UserCouponDeleteResponse deleteCoupon(
		Long userCouponId);

	/**
	 * UserCouponId로 쿠폰의 정보를 조회
	 */
	CouponSearchCouponIdInfo.UserCouponSearchCouponIdResponse findCoupon(
		Long userCouponId);

	CouponSearchUserIdInfo.UserCouponSearchCouponIdResponse findCouponInfoByUserId(
		Long userId);

	/**
	 * * User의 Id를 통해 User의 쿠폰리스트들을 조회
	 * @param request
	 * @return
	 */
	List<CouponListInfo.UserCouponListResponse> getUserCouponsByUserId(
		CouponListInfo.UserCouponListRequest request);
}

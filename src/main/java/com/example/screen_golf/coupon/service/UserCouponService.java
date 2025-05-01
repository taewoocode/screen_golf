package com.example.screen_golf.coupon.service;

import java.util.List;

import com.example.screen_golf.coupon.dto.UserCouponCreateInfo;
import com.example.screen_golf.coupon.dto.UserCouponDeleteInfo;
import com.example.screen_golf.coupon.dto.UserCouponListInfo;
import com.example.screen_golf.coupon.dto.UserCouponSearchCouponIdInfo;
import com.example.screen_golf.coupon.dto.UserCouponSearchUserIdInfo;

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

	UserCouponSearchUserIdInfo.UserCouponSearchCouponIdResponse findCouponInfoByUserId(
		Long userId);

	/**
	 * User의 Id를 통해 User의 쿠폰리스트들을 조회
	 */
	List<UserCouponListInfo.UserCouponListResponse> getUserCouponsByUserId(
		UserCouponCreateInfo.UserCouponCreateRequest request);
}

package com.example.screen_golf.coupon.service;

import com.example.screen_golf.coupon.domain.UserCoupon;

public interface UserCouponService {

	UserCoupon.UserCouponCreateResponse createCoupon(UserCoupon.UserCouponCreateRequest request);
}

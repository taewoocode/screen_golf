package com.example.screen_golf.reservation.service;

import java.math.BigDecimal;

import com.example.screen_golf.coupon.domain.UserCoupon;

public interface DiscountPolicy {
	BigDecimal applyDiscount(BigDecimal originalPrice, UserCoupon userCoupon);
}

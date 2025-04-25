package com.example.screen_golf.reservation.service;

import java.math.BigDecimal;

import com.example.screen_golf.coupon.domain.UserCoupon;

public class FixedAmountCountPolicy implements DiscountPolicy {
	@Override
	public BigDecimal applyDiscount(BigDecimal originalPrice, UserCoupon userCoupon) {
		BigDecimal discountAmount = BigDecimal.valueOf(userCoupon.getDiscountAmount());
		return originalPrice.subtract(discountAmount);
	}
}

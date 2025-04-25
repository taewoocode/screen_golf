package com.example.screen_golf.reservation.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.screen_golf.coupon.domain.UserCoupon;

/**
 * 고정할인정책
 */
@Component
public class FixedAmountCountPolicy implements DiscountPolicy {
	@Override
	public BigDecimal applyDiscount(BigDecimal originalPrice, UserCoupon userCoupon) {
		BigDecimal discountAmount = BigDecimal.valueOf(userCoupon.getDiscountAmount());
		return originalPrice.subtract(discountAmount);
	}
}

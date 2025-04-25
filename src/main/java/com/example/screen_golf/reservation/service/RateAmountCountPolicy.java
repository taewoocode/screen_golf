package com.example.screen_golf.reservation.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.screen_golf.coupon.domain.UserCoupon;

/**
 * 비율할인정책
 */
@Component
public class RateAmountCountPolicy implements DiscountPolicy {
	@Override
	public BigDecimal applyDiscount(BigDecimal originalPrice, UserCoupon userCoupon) {
		BigDecimal rate = BigDecimal.valueOf(userCoupon.getDiscountAmount())
			.divide(BigDecimal.valueOf(100)); // 10 → 0.10
		BigDecimal discount = originalPrice.multiply(rate);
		return originalPrice.subtract(discount);
	}
}

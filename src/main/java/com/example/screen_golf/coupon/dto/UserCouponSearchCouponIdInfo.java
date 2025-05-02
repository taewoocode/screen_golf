package com.example.screen_golf.coupon.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.coupon.domain.CouponPolicy;
import com.example.screen_golf.coupon.domain.CouponStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 쿠폰ID로 쿠폰의 정보를 조회한다.
 */
public class UserCouponSearchCouponIdInfo {

	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Getter
	public static class UserCouponSearchCouponIdResponse {
		private Long id;
		private Long userId;
		private String couponCode;
		private CouponPolicy couponPolicy;  // 쿠폰 정책을 포함
		private LocalDateTime validFrom;
		private LocalDateTime validTo;
		private CouponStatus status;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}
}

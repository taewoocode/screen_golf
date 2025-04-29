package com.example.screen_golf.coupon.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.coupon.domain.CouponStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User의 ID로 쿠폰의 정보 확인
 */
public class UserCouponSearchUserIdInfo {

	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Getter
	public static class UserCouponSearchCouponIdResponse {
		private Long userId;
		private Long id;
		private String couponCode;
		private String name;
		private Integer discountAmount;
		private LocalDateTime validFrom;
		private LocalDateTime validTo;
		private CouponStatus status;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}
}

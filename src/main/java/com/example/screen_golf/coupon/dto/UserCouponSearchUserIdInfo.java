package com.example.screen_golf.coupon.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.coupon.domain.CouponPolicy;
import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.user.domain.User;

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
		private CouponPolicy couponPolicy;
		private LocalDateTime validFrom;
		private LocalDateTime validTo;
		private CouponStatus status;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		public static UserCouponSearchCouponIdResponse toDto(User user, UserCoupon coupon, CouponPolicy policy) {
			return UserCouponSearchUserIdInfo.UserCouponSearchCouponIdResponse.builder()
				.userId(user.getId())
				.id(coupon.getId())
				.couponCode(coupon.getCouponCode())
				.name(policy.name())
				.couponPolicy(policy)
				.validFrom(coupon.getValidFrom())
				.validTo(coupon.getValidTo())
				.status(coupon.getStatus())
				.createdAt(coupon.getCreatedAt())
				.updatedAt(coupon.getUpdatedAt())
				.build();

		}
	}

}

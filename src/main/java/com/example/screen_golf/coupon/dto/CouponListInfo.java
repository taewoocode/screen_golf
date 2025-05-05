package com.example.screen_golf.coupon.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.coupon.domain.Coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponListInfo {

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserCouponListRequest {
		private Long userId;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserCouponListResponse {
		private Long id;
		private String couponCode;
		private String name;
		private Integer discountAmount;
		private LocalDateTime validFrom;
		private LocalDateTime validTo;
		private String status;

		public static UserCouponListResponse toDto(Coupon coupon) {
			return UserCouponListResponse.builder()
				.id(coupon.getId())
				.couponCode(coupon.getCouponCode())
				// .name(coupon.getName())
				// .discountAmount(coupon.getDiscountAmount())
				.validFrom(coupon.getValidFrom())
				.validTo(coupon.getValidTo())
				.status(coupon.getStatus().name()) // Enum을 String으로 반환
				.build();
		}
	}
}

package com.example.screen_golf.coupon.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserCouponCreateInfo {
	
	@Getter
	@NoArgsConstructor
	public static class UserCouponCreateRequest {
		private Long userId;         // 사용자 ID
		private String couponCode;   // 쿠폰 코드
		private String name;         // 쿠폰 이름
		private Integer discountAmount; // 할인 금액
		private LocalDateTime validFrom; // 쿠폰 유효 시작일
		private LocalDateTime validTo;   // 쿠폰 유효 종료일
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserCouponCreateResponse {

		private Long userCouponId;   // 생성된 쿠폰 ID
		private Long userId;         // 사용자 ID
		private String couponCode;   // 쿠폰 코드
		private String getCouponName;         // 쿠폰 이름
		private Integer discountAmount; // 할인 금액
		private LocalDateTime validFrom; // 쿠폰 유효 시작일
		private LocalDateTime validTo;   // 쿠폰 유효 종료일
		private LocalDateTime createdAt; // 생성일시
	}
}

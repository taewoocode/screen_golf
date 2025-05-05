package com.example.screen_golf.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponDeleteInfo {

	/**
	 * 사용자 쿠폰 삭제 요청 DTO
	 * 쿠폰 ID를 통해 쿠폰을 삭제 요청한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserCouponDeleteRequest {
		private Long userCouponId;
	}

	/**
	 * 사용자 쿠폰 삭제 응답 DTO
	 * 삭제 성공 여부를 전달한다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserCouponDeleteResponse {
		private Long userCouponId;
		private boolean success;
	}
}

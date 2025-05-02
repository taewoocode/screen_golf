package com.example.screen_golf.coupon.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserCouponCreateInfo {

	@Getter
	@NoArgsConstructor
	public static class UserCouponCreateRequest {
		private Long userId;
	}
}

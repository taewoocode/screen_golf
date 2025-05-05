package com.example.screen_golf.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointChargeInfo {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PointChargeRequest {
		private Long userId;
		private Integer amount;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PointChargeResponse {
		private Long userId;
		private Integer chargedAmount;  // 충전한 포인트
		private Integer totalPoint;     // 현재 보유 포인트
		private String message;
	}
}

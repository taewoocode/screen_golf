package com.example.screen_golf.payment.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentKakaoInfo {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class KakaoPaymentResponse {
		private String user_id;
		private String next_redirect_pc_url;  // PC에서 리디렉션할 URL
		private String next_redirect_mobile_url;  // 모바일에서 리디렉션할 URL
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class KakaoPaymentRequest {
		private String user_id;               // 사용자 ID
		private String item_name;             // 상품명
		private int total_amount;             // 총 금액
		private int tax_free_amount;          // 면세 금액 (없으면 0)
		private String approval_url;          // 결제 승인 후 리디렉션 URL
		private String cancel_url;            // 결제 취소 후 리디렉션 URL
		private String fail_url;              // 결제 실패 후 리디렉션 URL
	}
}

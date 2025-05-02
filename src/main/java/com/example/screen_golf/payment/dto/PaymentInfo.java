package com.example.screen_golf.payment.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.reservation.domain.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentInfo {

	/**
	 * 클라이언트로부터 결제 요청 시 전달받는 데이터
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentRequest {
		private Long reservationId;     // 이미 생성된 예약 ID (PENDING 상태)
		private Long userId;            // 결제하는 사용자 ID
		private Integer amount;         // 최종 결제 금액 (쿠폰 적용된 값)
		private String paymentMethod;   // 예: CARD, KAKAO_PAY 등
	}

	/**
	 * 결제 처리 후 클라이언트에게 반환하는 응답 데이터
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentResponse {
		private Long paymentId;
		private Long reservationId;
		private Long userId;
		private Integer amount;
		private String paymentMethod;
		private String status;
		private String transactionId;
		private LocalDateTime createdAt;
		private ReservationStatus reservationStatus;
	}
}

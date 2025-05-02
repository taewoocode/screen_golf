package com.example.screen_golf.payment.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.coupon.domain.CouponPolicy;
import com.example.screen_golf.coupon.domain.UserCoupon;
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
		private Long reservationId;
		private Integer amount;
		private String paymentMethod;
		private UserCoupon userCoupon;
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
		private CouponPolicy couponPolicy;
	}
}

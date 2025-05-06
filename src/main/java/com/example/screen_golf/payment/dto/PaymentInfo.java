package com.example.screen_golf.payment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.screen_golf.coupon.domain.Coupon;
import com.example.screen_golf.coupon.domain.CouponPolicy;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentInfo {

	/**
	 * 결제 요청 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentRequest {
		private Long userId;
		private Integer amount;

		// 쿠폰 ID (하나의 결제에 하나의 쿠폰만 적용)
		private Long couponId;

		// RoomPrice ID (가격 정보에 대한 참조)
		private Long loomId;
		private LocalDate reservationDate;
		private LocalDateTime startTime;
		private LocalDateTime endTime;
	}

	/**
	 * 결제 상태 업데이트 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentStatusUpdateRequest {
		private String status;           // 결제 상태 (COMPLETED, FAILED, REFUNDED)
		private String transactionId;    // 거래 ID (선택사항)
	}

	/**
	 * 결제 응답 DTO
	 */
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentResponse {
		private Long paymentId;          // 결제 ID
		private Long userId;             // 사용자 ID
		private Integer amount;          // 결제 금액
		private String paymentMethod;    // 결제 수단
		private PaymentStatus status;    // 결제 상태
		private String transactionId;    // 거래 ID
		private LocalDateTime createdAt; // 결제 생성 시간
		private CouponPolicy couponPolicy; // 사용된 쿠폰 정책
		private Integer discountAmount;  // 할인 금액
		private Long couponId;           // 사용된 쿠폰 ID
		private String message;          // 응답 메시지
		private String redirectUrl;
		private String paymentKey;       // 카카오페이 결제 키
		private String orderId;          // 주문 ID

		public static PaymentResponse toDto(Payment payment, Coupon userCoupon) {
			return PaymentResponse.builder()
				.paymentId(payment.getId())
				.userId(payment.getUser().getId())
				.amount(payment.getAmount())
				.paymentMethod(payment.getPaymentMethod())
				.status(payment.getStatus())
				.transactionId(payment.getTransactionId())
				.createdAt(payment.getCreatedAt())
				.couponPolicy(userCoupon != null ? userCoupon.getCouponPolicy() : null)
				.discountAmount(
					userCoupon != null ? userCoupon.getCouponPolicy().calculateDiscount(payment.getAmount()) : 0)
				.couponId(userCoupon != null ? userCoupon.getId() : null)
				.message(payment.getMessage())
				.paymentKey(payment.getPaymentKey())
				.orderId(payment.getTransactionId())
				.build();
		}
	}
}

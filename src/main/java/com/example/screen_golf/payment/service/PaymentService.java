package com.example.screen_golf.payment.service;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.payment.dto.PaymentInfo;

public interface PaymentService {
	/**
	 * 결제 요청을 처리합니다.
	 * @param request 결제 요청 정보
	 * @return 결제 처리 결과
	 */
	PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request);

	/**
	 * 결제 승인을 처리합니다.
	 * @param paymentKey 결제 키
	 * @param orderId 주문 ID
	 * @param amount 결제 금액
	 * @param pgToken 결제 토큰
	 * @return 결제 처리 결과
	 */
	@Transactional
	PaymentInfo.PaymentResponse approvePayment(String paymentKey, String orderId, Integer amount, String pgToken);

	/**
	 * 결제 취소를 처리합니다.
	 * @param paymentKey 결제 키
	 * @param cancelReason 취소 사유
	 * @return 결제 처리 결과
	 */
	PaymentInfo.PaymentResponse cancelPayment(String paymentKey, String cancelReason);
}

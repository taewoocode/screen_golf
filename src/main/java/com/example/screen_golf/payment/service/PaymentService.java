package com.example.screen_golf.payment.service;

import com.example.screen_golf.payment.dto.PaymentInfo;

public interface PaymentService {
	/**
	 * 결제 요청을 처리합니다.
	 * @param request 결제 요청 정보
	 * @return 결제 처리 결과
	 */
	PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request);

	/**
	 * 결제 상태를 업데이트합니다.
	 * @param paymentId 결제 ID
	 * @param status 새로운 결제 상태
	 * @param transactionId 거래 ID (선택사항)
	 */
	void updatePaymentStatus(Long paymentId, String status, String transactionId);
}

package com.example.screen_golf.payment.service;

import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.reservation.domain.Reservation;

public interface PaymentService {
	/**
	 * 결제 요청을 처리합니다.
	 * @param request 결제 요청 정보
	 * @return 결제 처리 결과
	 */
	PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request);

	void approve(Reservation reservation);
}

package com.example.screen_golf.payment.service;

import com.example.screen_golf.payment.domain.Payment;

public interface PaymentService {

	/**
	 * 결제 요청
	 * @param request
	 * @return
	 */
	Payment.PaymentResponse requestPayment(Payment.PaymentRequest request);

}

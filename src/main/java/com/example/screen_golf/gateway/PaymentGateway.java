package com.example.screen_golf.gateway;

import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.dto.PaymentInfo;

public interface PaymentGateway {
	PaymentInfo.PaymentResponse requestPayment(Payment payment);

	PaymentInfo.PaymentResponse approvePayment(String paymentKey, String orderId, Integer amount, String pgToken);

	PaymentInfo.PaymentResponse cancelPayment(String paymentKey, String cancelReason);
}

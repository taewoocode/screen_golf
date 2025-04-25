package com.example.screen_golf.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.service.PaymentService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "Payment", description = "결제관련 API 입니다.")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@Operation(
		summary = SwaggerDocs.SUMMARY_REQUEST_PAYMENT,
		description = SwaggerDocs.DESCRIPTION_REQUEST_PAYMENT
	)
	@PostMapping
	public ResponseEntity<Payment.PaymentResponse> requestPayment(
		@Parameter(name = "결제 요청 정보", required = true)
		@RequestBody Payment.PaymentRequest request
	) {
		Payment.PaymentResponse paymentResponse = paymentService.requestPayment(request);
		log.info("결제 요청 성공 - Payment ID={}, 사용자 ID={}", paymentResponse.getPaymentId(), paymentResponse.getUserId());
		return ResponseEntity.ok(paymentResponse);
	}

}

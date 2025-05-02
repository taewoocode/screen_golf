package com.example.screen_golf.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.jwts.CustomUserDetails;
import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.payment.service.PaymentService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment", description = "결제 관련 API")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@Operation(
		summary = SwaggerDocs.SUMMARY_REQUEST_PAYMENT,
		description = SwaggerDocs.DESCRIPTION_REQUEST_PAYMENT
	)
	@PostMapping
	public ResponseEntity<PaymentInfo.PaymentResponse> requestPayment(
		@RequestBody PaymentInfo.PaymentRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("결제 요청 - 예약 ID: {}, 금액: {}, 결제 수단: {}", 
			request.getReservationId(), request.getAmount(), request.getPaymentMethod());
		
		PaymentInfo.PaymentResponse response = paymentService.requestPayment(request);
		
		log.info("결제 처리 완료 - 결제 ID: {}, 상태: {}", 
			response.getPaymentId(), response.getStatus());
		
		return ResponseEntity.ok(response);
	}
}

package com.example.screen_golf.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.screen_golf.gateway.dto.KakaoPayApproveRequest;
import com.example.screen_golf.gateway.dto.KakaoPayApproveResponse;
import com.example.screen_golf.gateway.dto.KakaoPayCancelRequest;
import com.example.screen_golf.gateway.dto.KakaoPayCancelResponse;
import com.example.screen_golf.gateway.dto.KakaoPayReadyRequest;
import com.example.screen_golf.gateway.dto.KakaoPayReadyResponse;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPayGateway implements PaymentGateway {
	private final RestTemplate restTemplate;
	private final PaymentRepository paymentRepository;

	@Value("${kakaopay.client-id}")
	private String cid;

	@Value("${kakaopay.secret-key}")
	private String secretKey;

	@Override
	public PaymentInfo.PaymentResponse requestPayment(Payment payment) {
		KakaoPayReadyRequest kakaoRequest = KakaoPayReadyRequest.builder()
			.cid(cid)
			.partner_order_id(payment.getId().toString())
			.partner_user_id(payment.getUser().getId().toString())
			.item_name("스크린 골프 예약")
			.quantity(1)
			.total_amount(payment.getAmount())
			.tax_free_amount(0)
			.approval_url("http://localhost:8080/api/v1/payments/approve")
			.cancel_url("http://localhost:8080/api/v1/payments/cancel")
			.fail_url("http://localhost:8080/api/v1/payments/fail")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + secretKey);
		headers.set("Content-type", "application/json");

		HttpEntity<KakaoPayReadyRequest> request = new HttpEntity<>(kakaoRequest, headers);
		KakaoPayReadyResponse kakaoResponse = restTemplate.postForObject(
			"https://kapi.kakao.com/v1/payment/ready",
			request,
			KakaoPayReadyResponse.class
		);

		payment.completePayment(kakaoResponse.getTid());
		payment.setStatus(PaymentStatus.PENDING);
		paymentRepository.save(payment);

		return PaymentInfo.PaymentResponse.builder()
			.paymentId(payment.getId())
			.status(payment.getStatus())
			.amount(payment.getAmount())
			.redirectUrl(kakaoResponse.getNext_redirect_pc_url())
			.build();
	}

	@Override
	public PaymentInfo.PaymentResponse approvePayment(String paymentKey, String orderId, Integer amount,
		String pgToken) {
		Payment payment = paymentRepository.findByPaymentKey(paymentKey)
			.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

		KakaoPayApproveRequest approveRequest = KakaoPayApproveRequest.builder()
			.cid(cid)
			.tid(paymentKey)
			.partner_order_id(orderId)
			.partner_user_id(payment.getUser().getId().toString())
			.pg_token(pgToken)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + secretKey);
		headers.set("Content-type", "application/json");

		HttpEntity<KakaoPayApproveRequest> request = new HttpEntity<>(approveRequest, headers);
		KakaoPayApproveResponse approveResponse = restTemplate.postForObject(
			"https://kapi.kakao.com/v1/payment/approve",
			request,
			KakaoPayApproveResponse.class
		);

		payment.setStatus(PaymentStatus.COMPLETED);
		paymentRepository.save(payment);

		return PaymentInfo.PaymentResponse.builder()
			.paymentId(payment.getId())
			.status(payment.getStatus())
			.amount(payment.getAmount())
			.build();
	}

	@Override
	public PaymentInfo.PaymentResponse cancelPayment(String paymentKey, String cancelReason) {
		Payment payment = paymentRepository.findByPaymentKey(paymentKey)
			.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

		KakaoPayCancelRequest cancelRequest = KakaoPayCancelRequest.builder()
			.cid(cid)
			.tid(paymentKey)
			.cancel_amount(payment.getAmount())
			.cancel_tax_free_amount(0)
			.cancel_reason(cancelReason)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + secretKey);
		headers.set("Content-type", "application/json");

		HttpEntity<KakaoPayCancelRequest> request = new HttpEntity<>(cancelRequest, headers);
		restTemplate.postForObject(
			"https://kapi.kakao.com/v1/payment/cancel",
			request,
			KakaoPayCancelResponse.class
		);

		// payment.setStatus(PaymentStatus.CANCELED);
		paymentRepository.save(payment);

		return PaymentInfo.PaymentResponse.builder()
			.paymentId(payment.getId())
			.status(payment.getStatus())
			.amount(payment.getAmount())
			.build();
	}
}

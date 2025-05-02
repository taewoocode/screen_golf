package com.example.screen_golf.payment.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.repository.UserCouponRepository;
import com.example.screen_golf.exception.payment.PaymentNotCompletedException;
import com.example.screen_golf.jwts.CustomUserDetails;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentHistory;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.payment.repository.PaymentHistoryRepository;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationStatus;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;

	@Override
	@Transactional
	public PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		Long userId = Long.parseLong(userDetails.getUsername());

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Reservation reservation = reservationRepository.findById(request.getReservationId())
			.orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

		// 쿠폰 조회 (선택사항)
		UserCoupon userCoupon = null;
		if (request.getUserCouponId() != null) {
			userCoupon = userCouponRepository.findById(request.getUserCouponId())
				.orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
		}

		Payment payment = Payment.createPayment(
			userId,
			reservation,
			request.getAmount(),
			request.getPaymentMethod(),
			userCoupon
		);

		// 결제 저장
		Payment savedPayment = paymentRepository.save(payment);

		// 결제 이력 생성
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(savedPayment)
			.previousStatus(null)
			.newStatus(PaymentStatus.PENDING)
			.reason("결제 요청")
			.build();

		paymentHistoryRepository.save(paymentHistory);

		try {
			processPayment(savedPayment, "TEMP_TRANSACTION_ID");

			reservation.changeStatus(ReservationStatus.CONFIRMED);
			reservationRepository.save(reservation);

			if (userCoupon != null) {
				userCoupon.use();
				userCouponRepository.save(userCoupon);
			}

			return PaymentInfo.PaymentResponse.toDto(savedPayment, userCoupon);
		} catch (Exception e) {
			processPaymentFailure(savedPayment);
			throw new PaymentNotCompletedException("결제 처리 중 오류가 발생했습니다.");
		}
	}

	private void processPayment(Payment payment, String transactionId) {
		PaymentStatus previousStatus = payment.getStatus();

		payment.completePayment(transactionId);
		paymentRepository.save(payment);

		PaymentHistory successHistory = PaymentHistory.builder()
			.payment(payment)
			.previousStatus(previousStatus)
			.newStatus(PaymentStatus.COMPLETED)
			.reason("결제 성공")
			.build();

		paymentHistoryRepository.save(successHistory);
	}

	private void processPaymentFailure(Payment payment) {
		PaymentStatus previousStatus = payment.getStatus();

		payment.failPayment();
		paymentRepository.save(payment);

		PaymentHistory failureHistory = PaymentHistory.builder()
			.payment(payment)
			.previousStatus(previousStatus)
			.newStatus(PaymentStatus.FAILED)
			.reason("결제 실패")
			.build();

		paymentHistoryRepository.save(failureHistory);
	}

	@Override
	@Transactional
	public void updatePaymentStatus(Long paymentId, String status, String transactionId) {
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

		PaymentStatus previousStatus = payment.getStatus();
		PaymentStatus newStatus = PaymentStatus.valueOf(status);

		if (newStatus == PaymentStatus.COMPLETED) {
			payment.completePayment(transactionId);
		} else if (newStatus == PaymentStatus.FAILED) {
			payment.failPayment();
		} else if (newStatus == PaymentStatus.REFUNDED) {
			payment.refund();
		}

		paymentRepository.save(payment);

		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(payment)
			.previousStatus(previousStatus)
			.newStatus(newStatus)
			.reason("결제 상태 업데이트")
			.build();

		paymentHistoryRepository.save(paymentHistory);
	}
}

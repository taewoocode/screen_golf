package com.example.screen_golf.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentHistory;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.repository.PaymentHistoryRepository;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.repository.ReservationRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final ReservationRepository reservationRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	@Override
	@Transactional
	public Payment.PaymentResponse requestPayment(Payment.PaymentRequest request) {

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(IllegalArgumentException::new);

		Reservation reservation = reservationRepository.findById(request.getReservationId())
			.orElseThrow(IllegalArgumentException::new);

		//엔티티생성
		Payment payment = Payment.builder()
			.user(user)
			.reservation(reservation)
			.amount(request.getAmount())
			.paymentMethod(request.getPaymentMethod())
			.build();
		Payment savedPayment = paymentRepository.save(payment);

		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(savedPayment)
			.previousStatus(PaymentStatus.PENDING)
			.newStatus(savedPayment.getStatus())
			.build();
		PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);

		Payment.PaymentResponse paymentResponse = Payment.PaymentResponse.builder()
			.paymentId(savedPayment.getId())
			.paymentMethod(savedPayment.getPaymentMethod())
			.amount(savedPayment.getAmount())
			.reservationId(savedPayment.getReservation().getId())
			.createdAt(savedPayment.getCreatedAt())
			.transactionId(savedPayment.getTransactionId())
			.status(savedPayment.getStatus().toString())
			.userId(savedPayment.getUser().getId())
			.build();
		return paymentResponse;
	}
}

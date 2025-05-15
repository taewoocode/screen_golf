package com.example.screen_golf.payment.service;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.service.CouponService;
import com.example.screen_golf.gateway.PaymentGateway;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.dto.PaymentConverter;
import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.point.dto.PointChargeInfo;
import com.example.screen_golf.point.service.PointService;
import com.example.screen_golf.reservation.dto.ReservationConverter;
import com.example.screen_golf.reservation.dto.ReservationInfo;
import com.example.screen_golf.reservation.service.ReservationService;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.repository.RoomRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;
	private final CouponService couponService;
	private final PointService pointService;
	private final PaymentGateway paymentGateway;
	private final PaymentConverter paymentConverter;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ReservationService reservationService;
	private final ReservationConverter reservationConverter;

	/**
	 * 결제 요청 처리
	 * 1. 사용자, 방 정보 조회
	 * 2. 쿠폰 유효성 검증 및 적용
	 * 3. 포인트 사용 가능 여부 확인 및 처리
	 * 4. Payment 엔티티 생성
	 * 5. 카카오페이 결제 준비 요청
	 */
	@Override
	public PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		Room room = roomRepository.findById(request.getRoomId())
			.orElseThrow(() -> new IllegalArgumentException("해당 방을 찾을 수 없습니다."));

		Integer finalAmount = request.getOriginalAmount();

		if (request.getCouponId() != null) {
			finalAmount = couponService.validateAndUseCoupon(request.getCouponId(), finalAmount);
		}

		if (request.getUsePoint() > 0) {
			finalAmount = pointService.validateAndUsePoint(request.getUserId(), request.getUsePoint(), finalAmount);
		}

		Payment payment = paymentConverter.makePaymentEntity(user, room, finalAmount);
		Payment savedPayment = paymentRepository.save(payment);

		return paymentGateway.requestPayment(savedPayment);
	}

	/**
	 * 결제 승인 처리
	 * 1. Payment 엔티티 조회
	 * 2. 카카오페이 결제 승인 요청
	 * 3. 결제 상태 업데이트 (COMPLETED)
	 * 4. 예약 생성 (동기 처리)
	 * 5. 포인트 적립 요청 (Kafka)
	 * 6. 디스코드 알림 요청 (Kafka)
	 */
	@Override
	@Transactional
	public PaymentInfo.PaymentResponse approvePayment(String paymentKey, String orderId, Integer amount,
		LocalDateTime startTime, LocalDateTime endTime) {
		Payment payment = paymentRepository.findByPaymentKey(paymentKey)
			.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

		try {
			PaymentInfo.PaymentResponse response = paymentGateway.approvePayment(paymentKey, orderId, amount);

			payment.setStatus(PaymentStatus.COMPLETED);
			payment.setMessage("결제가 완료되었습니다.");
			paymentRepository.save(payment);

			ReservationInfo.ReservationRequest reservationRequest = reservationConverter.toMakeCreateReservation(
				payment, startTime, endTime);
			reservationService.createReservation(reservationRequest);
			log.info("예약 생성 완료={}", reservationRequest);

			accumlatePointToKafka(amount, payment);
			discordNotificateToKafka(orderId, amount, payment);

			return response;
		} catch (Exception e) {
			payment.setStatus(PaymentStatus.FAILED);
			payment.setMessage("결제가 실패했습니다: " + e.getMessage());
			paymentRepository.save(payment);
			throw e;
		}
	}

	private void discordNotificateToKafka(String orderId, Integer amount, Payment payment) {
		PaymentInfo.DiscordNotificationRequest notificationRequest = new PaymentInfo.DiscordNotificationRequest(
			orderId, amount, payment.getUser().getName(), (int)(amount * 0.1));
		kafkaTemplate.send("discord-notifications", notificationRequest);
		log.info("디스코드 알림 요청 전송 - 주문번호={}", orderId);
	}

	private void accumlatePointToKafka(Integer amount, Payment payment) {
		PointChargeInfo.PointChargeRequest pointRequest = new PointChargeInfo.PointChargeRequest(
			payment.getUser().getId(),
			(int)(amount * 0.1)
		);
		kafkaTemplate.send("point-accumulation", pointRequest);
		log.info("포인트 적립 요청 전송 - 사용자={}, 적립 금액={}", payment.getUser().getId(), (int)(amount * 0.1));
	}

	@Override
	@Transactional
	public PaymentInfo.PaymentResponse cancelPayment(String paymentKey, String cancelReason) {
		return paymentGateway.cancelPayment(paymentKey, cancelReason);
	}
}

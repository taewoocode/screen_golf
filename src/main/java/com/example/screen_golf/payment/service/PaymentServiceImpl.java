package com.example.screen_golf.payment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.Coupon;
import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.repository.CouponRepository;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.payment.gateway.PaymentGateway;
import com.example.screen_golf.payment.repository.PaymentRepository;
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
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;
	private final RoomRepository roomRepository;
	private final PaymentGateway paymentGateway;
	private final ReservationService reservationService;

	/**
	 * // 결제 객체 생성 (상태는 PENDING) -> 예약 승인 후 결제 완료로 변경(approve)
	 * @param request 결제 요청 정보
	 * @return
	 */
	@Override
	@Transactional
	public PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Room room = roomRepository.findById(request.getLoomId())
			.orElseThrow(() -> new IllegalArgumentException("해당 방을 찾을 수 없습니다."));

		Integer price = room.calculatePrice(request.getReservationDate(), request.getStartTime(), request.getEndTime());

		List<Coupon> availableCoupons = couponRepository.findAvailableCoupons(user.getId(), CouponStatus.UNUSED,
			LocalDateTime.now());

		// 쿠폰 적용
		Integer finalAmount = applyCoupon(request, availableCoupons, price);

		Payment payment = Payment.builder()
			.user(user)
			.room(room)
			.amount(finalAmount)
			.paymentMethod("KAKAOPAY")
			.status(PaymentStatus.PENDING)
			.message("결제가 진행 중입니다.")
			.build();

		Payment savedPayment = paymentRepository.save(payment);

		return paymentGateway.requestPayment(savedPayment);
	}

	@Override
	@Transactional
	public PaymentInfo.PaymentResponse approvePayment(String paymentKey, String orderId, Integer amount,
		LocalDateTime startTime, LocalDateTime endTime) {
		// 1. Payment 조회
		Payment payment = paymentRepository.findByPaymentKey(paymentKey)
			.orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

		try {
			// 2. 카카오페이 결제 승인
			PaymentInfo.PaymentResponse response = paymentGateway.approvePayment(paymentKey, orderId, amount);

			// 3. Payment 상태를 COMPLETED로 업데이트
			payment.setStatus(PaymentStatus.COMPLETED);
			payment.setMessage("결제가 완료되었습니다.");
			paymentRepository.save(payment);

			// 4. 예약 생성 (결제 후 예약을 생성)
			ReservationInfo.ReservationRequest reservationRequest = new ReservationInfo.ReservationRequest(
				payment.getUser().getId(),
				payment.getRoom().getId(),
				startTime,
				endTime,
				payment.getId()
			);

			reservationService.createReservation(reservationRequest);

			return response;
		} catch (Exception e) {
			// 6. 결제 실패 시 상태 업데이트
			payment.setStatus(PaymentStatus.FAILED);
			payment.setMessage("결제가 실패했습니다: " + e.getMessage());
			paymentRepository.save(payment);
			throw e;
		}
	}

	@Override
	@Transactional
	public PaymentInfo.PaymentResponse cancelPayment(String paymentKey, String cancelReason) {
		return paymentGateway.cancelPayment(paymentKey, cancelReason);
	}

	private Integer calculateFinalAmount(Integer amount, Coupon coupon) {
		if (coupon != null) {
			return coupon.calculateDiscount(amount);
		}
		return amount;
	}

	// 쿠폰 적용 처리 로직
	private Integer applyCoupon(PaymentInfo.PaymentRequest request, List<Coupon> availableCoupons, Integer price) {
		if (request.getCouponId() != null) {
			Coupon coupon = availableCoupons.stream()
				.filter(c -> c.getId().equals(request.getCouponId()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("유효한 쿠폰을 찾을 수 없습니다."));

			// 쿠폰 유효성 체크
			if (!coupon.isValid()) {
				throw new IllegalArgumentException("유효하지 않은 쿠폰입니다.");
			}
			if (!coupon.isAvailable()) {
				throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
			}

			Integer discountAmount = coupon.calculateDiscount(price);
			Integer finalAmount = price - discountAmount;

			if (finalAmount < 0) {
				finalAmount = 0;
			}
			return finalAmount;
		}

		return price;
	}
}

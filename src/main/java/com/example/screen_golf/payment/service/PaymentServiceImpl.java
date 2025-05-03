package com.example.screen_golf.payment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.coupon.domain.Coupon;
import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.repository.CouponRepository;
import com.example.screen_golf.exception.payment.PaymentNotCompletedException;
import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.payment.dto.PaymentInfo;
import com.example.screen_golf.payment.repository.PaymentRepository;
import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.room.domain.RoomPrice;
import com.example.screen_golf.room.repository.RoomPriceRepository;
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
	private final RoomPriceRepository roomPriceRepository;

	/**
	 * // 결제 객체 생성 (상태는 PENDING) -> 예약 승인 후 결제 완료로 변경(approve)
	 * @param request 결제 요청 정보
	 * @return
	 */
	@Override
	@Transactional
	public PaymentInfo.PaymentResponse requestPayment(PaymentInfo.PaymentRequest request) {
		// 사용자 조회
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 룸 가격정보 조회
		RoomPrice roomPrice = roomPriceRepository.findByRoom(request.getRoomPriceId())
			.orElseThrow(() -> new IllegalArgumentException("해당 가격 정보를 찾을 수 없습니다."));

		// 사용가능한 쿠폰정보 조회
		List<Coupon> availableCoupons = couponRepository.findAvailableCoupons(user.getId(), CouponStatus.UNUSED,
			LocalDateTime.now());

		Integer price = roomPrice.getPrice();

		Coupon coupon = null;
		Integer discountAmount = 0;
		Integer finalAmount = price;

		// 쿠폰이 존재하고 유효한 쿠폰이 있다면 && 선택한 쿠폰이 유효한지 확인
		if (request.getCouponId() != null) {

			coupon = availableCoupons.stream()
				.filter(c -> c.getId().equals(request.getCouponId()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("유효한 쿠폰을 찾을 수 없습니다."));

			if (!coupon.isValid()) {
				throw new IllegalArgumentException("유효하지 않은 쿠폰입니다.");
			}
			if (!coupon.isAvailable()) {
				throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
			}

			discountAmount = coupon.calculateDiscount(price);
			finalAmount = price - discountAmount;
			if (finalAmount < 0) {
				finalAmount = 0;
			}
		}

		// 결제 객체 생성 (상태는 PENDING)
		Payment payment = Payment.builder()
			.user(user)
			.amount(finalAmount)
			.paymentMethod("CARD")
			.status(PaymentStatus.PENDING)
			.coupon(coupon)  // coupon이 null일 수 있으므로
			.message("결제가 진행 중입니다.")
			.build();

		// 결제 객체 저장 (최종 결제 승인 전에 상태는 PENDING)
		Payment savedPayment = paymentRepository.save(payment);

		return PaymentInfo.PaymentResponse.toDto(savedPayment, coupon);  // coupon이 null일 수 있음
	}

	@Override
	@Transactional
	public void approve(Reservation reservation) {
		Payment payment = reservation.getPayment();

		if (payment == null) {
			throw new PaymentNotCompletedException("예약에 결제 정보가 없습니다.");
		}

		if (payment.getStatus() == PaymentStatus.COMPLETED) {
			log.info("이미 결제가 완료된 상태입니다. 결제 ID={}", payment.getId());
			return;
		}

		try {
			// 결제 금액 계산 전에 쿠폰이 있으면 적용하여 할인 금액을 계산
			Coupon coupon = payment.getCoupon();
			if (coupon != null) {
				int discountAmount = coupon.calculateDiscount(payment.getAmount());
				payment = payment.builder()
					.amount(payment.getAmount() - discountAmount)
					.message("결제가 진행 중입니다. 쿠폰 적용: " + coupon.getCouponCode())
					.build();

				coupon.use();
				couponRepository.save(coupon);
				log.info("쿠폰 사용 완료: 쿠폰 ID={}", coupon.getId());
			}

			// 결제 처리
			payment = payment.builder()
				.status(PaymentStatus.COMPLETED)
				.transactionId("TXN_" + System.currentTimeMillis())  // 트랜잭션 ID 갱신
				.message("결제가 완료되었습니다.")
				.build();
			paymentRepository.save(payment);

			log.info("결제 승인 완료: 결제 ID={}", payment.getId());

		} catch (Exception e) {
			payment = payment.builder()
				.status(PaymentStatus.FAILED)
				.message("결제 승인 실패: " + e.getMessage())
				.build();
			paymentRepository.save(payment);
			log.error("결제 승인 중 오류 발생", e);
			throw new PaymentNotCompletedException("결제 승인 중 오류가 발생했습니다.", e);
		}
	}

	private void processPaymentApproval(Payment payment, Coupon coupon) {
		try {
			// 결제 승인 처리
			log.info("결제 승인 처리 시작: 결제 ID={}, 사용자 ID={}", payment.getId(), payment.getUser().getId());

			// 결제 완료 상태로 변경
			Object success = PaymentStatus.COMPLETED;
			payment.setMessage("결제가 완료되었습니다.");
			payment.setTransactionId("TXN_" + System.currentTimeMillis());

			paymentRepository.save(payment);

			// 쿠폰 사용 처리 (쿠폰이 있으면)
			if (coupon != null) {
				coupon.use();
				couponRepository.save(coupon);
				log.info("쿠폰 사용 처리 완료: 쿠폰 ID={}", coupon.getId());
			}
		} catch (Exception e) {
			log.error("결제 승인 처리 중 오류 발생: {}", e.getMessage(), e);
			Object failed = PaymentStatus.FAILED;
			payment.setMessage("결제 승인 실패: " + e.getMessage());
			paymentRepository.save(payment);
			throw new PaymentNotCompletedException("결제 승인 처리 중 오류가 발생했습니다.", e);
		}
	}

	private void processPaymentFailure(Payment payment) {
		// 결제 실패 처리
		Object failed = PaymentStatus.FAILED;
		payment.setMessage("결제가 실패했습니다.");
		paymentRepository.save(payment);
		log.error("결제 실패: 결제 ID={}", payment.getId());
	}

}

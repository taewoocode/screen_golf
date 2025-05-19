package com.example.screen_golf.payment.dto;

import org.springframework.stereotype.Component;

import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.payment.domain.PaymentStatus;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.user.domain.User;
import java.time.LocalDateTime;

@Component
public class PaymentConverter {

	public Payment makePaymentEntity(User user, Room room, Integer finalAmount, LocalDateTime startTime, LocalDateTime endTime) {
		return Payment.builder()
			.user(user)
			.room(room)
			.amount(finalAmount)
			.paymentMethod("KAKAOPAY")
			.status(PaymentStatus.PENDING)
			.message("결제가 진행 중입니다.")
			.reservationStartTime(startTime)
			.reservationEndTime(endTime)
			.build();
	}
}

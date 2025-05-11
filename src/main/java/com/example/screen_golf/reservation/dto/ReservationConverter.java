package com.example.screen_golf.reservation.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.screen_golf.payment.domain.Payment;
import com.example.screen_golf.room.domain.Room;

@Component
public class ReservationConverter {
	public ReservationInfo.AvailableRoomResponse toAvailableRoomResponse(Room room) {
		return ReservationInfo.AvailableRoomResponse.builder()
			.roomId(room.getId())
			.roomName(room.getName())
			.roomType(room.getRoomType())
			.pricePerHour(room.getPricePerHour())
			.description(room.getDescription())
			.build();
	}

	public ReservationInfo.ReservationRequest toMakeCreateReservation(Payment payment, LocalDateTime startTime,
		LocalDateTime endTime) {
		return ReservationInfo.ReservationRequest.builder()
			.userId(payment.getUser().getId())
			.roomId(payment.getRoom().getId())
			.startTime(startTime)
			.endTime(endTime)
			.paymentId(payment.getId())
			.build();

	}
}

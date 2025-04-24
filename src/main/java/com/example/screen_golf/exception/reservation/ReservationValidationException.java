package com.example.screen_golf.exception.reservation;

public class ReservationValidationException extends RuntimeException {

	/**
	 * 유효성 검사를 검증
	 * @param message
	 */
	public ReservationValidationException(String message) {
		super(message);
	}
}

package com.example.screen_golf.exception.reservation;

public class ReservationConflictException extends RuntimeException {

	/**
	 * 예외 중복 추가
	 * @param message
	 */
	public ReservationConflictException(String message) {
		super(message);
	}
}

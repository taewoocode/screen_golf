package com.example.screen_golf.exception.payment;

public class PaymentNotCompletedException extends RuntimeException {
	public PaymentNotCompletedException(String message) {
		super(message);
	}

	public PaymentNotCompletedException(String message, Throwable cause) {
		super(message, cause);
	}
}

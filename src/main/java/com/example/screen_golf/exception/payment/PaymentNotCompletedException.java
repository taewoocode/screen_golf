package com.example.screen_golf.exception.payment;

public class PaymentNotCompletedException extends RuntimeException {
	public PaymentNotCompletedException(String message) {
		super(message);
	}
}

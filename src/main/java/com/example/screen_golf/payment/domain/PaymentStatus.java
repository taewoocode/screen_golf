package com.example.screen_golf.payment.domain;

public enum PaymentStatus {
	PENDING,        // 결제 대기
	COMPLETED,      // 결제 완료
	FAILED,         // 결제 실패
	CANCELED        // 결제 취소
} 
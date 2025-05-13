package com.example.screen_golf.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private Long paymentId;
    private int amount;
    private int pointAmount;  // 사용한 포인트 금액
} 
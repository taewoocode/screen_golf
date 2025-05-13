package com.example.screen_golf.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
    private Long userId;
    private Long roomId;
    private Long couponId;
    private String paymentMethod;
    private int usePoint;  // 사용할 포인트 금액
} 
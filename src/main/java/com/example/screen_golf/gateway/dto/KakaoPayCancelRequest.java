package com.example.screen_golf.gateway.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoPayCancelRequest {
	private String cid;
	private String tid;
	private Integer cancel_amount;
	private Integer cancel_tax_free_amount;
	private String cancel_reason;
} 
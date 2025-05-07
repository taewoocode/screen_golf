package com.example.screen_golf.gateway.dto;

import lombok.Getter;

@Getter
public class KakaoPayCancelResponse {
	private String aid;
	private String tid;
	private String cid;
	private String status;
	private String partner_order_id;
	private String partner_user_id;
	private String payment_method_type;
	private Amount amount;
	private Amount canceled_amount;
	private String canceled_at;
	private String payload;

	@Getter
	public static class Amount {
		private Integer total;
		private Integer tax_free;
		private Integer vat;
		private Integer point;
		private Integer discount;
	}
} 
package com.example.screen_golf.coupon.domain;

public enum CouponPolicy {
	FIXED_5000(CouponType.FIXED, 5000, null),
	FIXED_10000(CouponType.FIXED, 10000, null),
	RATE_15(CouponType.RATE, 15, null),
	RATE_30(CouponType.RATE, 30, 8000),
	RATE_50(CouponType.RATE, 50, 10000);

	private final CouponType type;
	private final int discountValue;
	private final Integer maxDiscountAmount;

	CouponPolicy(CouponType type, int discountValue, Integer maxDiscountAmount) {
		this.type = type;
		this.discountValue = discountValue;
		this.maxDiscountAmount = maxDiscountAmount;
	}

	public CouponType getType() {
		return type;
	}

	public int getDiscountValue() {
		return discountValue;
	}

	public Integer getMaxDiscountAmount() {
		return maxDiscountAmount;
	}

	/**
	 *
	 * - FIXED 타입의 경우 할인 금액을 고정값으로 반환.
	 * - RATE 타입의 경우 원래 금액에 비율을 적용하여 계산한 후, maxDiscountAmount가 있으면 최대 금액을 적용.
	 */
	public Integer calculateDiscount(Integer amount) {
		switch (this.type) {
			case FIXED:
				return Math.min(amount, this.discountValue);
			case RATE:
				int discount = amount * this.discountValue / 100;
				if (this.maxDiscountAmount != null) {
					discount = Math.min(discount, this.maxDiscountAmount);
				}
				return discount;
			default:
				throw new IllegalArgumentException("지원되지 않는 할인 정책입니다.");
		}
	}
}

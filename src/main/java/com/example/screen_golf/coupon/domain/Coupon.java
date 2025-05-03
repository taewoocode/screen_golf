package com.example.screen_golf.coupon.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 사용자 연관 */
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** 쿠폰 정책 연관 */
	/** 쿠폰 정책 타입 - enum을 직접 저장 */
	@Enumerated(EnumType.STRING)
	@Column(name = "coupon_policy", nullable = false)
	private CouponPolicy couponPolicy;

	/** 쿠폰 식별 코드 */
	@Column(nullable = false)
	private String couponCode;

	/** 유효 기간 */
	@Column(nullable = false)
	private LocalDateTime validFrom;

	@Column(nullable = false)
	private LocalDateTime validTo;

	/** 상태 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CouponStatus status;

	/** 생성/수정 시간 */
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private boolean used;

	@Builder
	public Coupon(User user, String couponCode, CouponPolicy couponPolicy,
		LocalDateTime validFrom, LocalDateTime validTo) {
		this.user = user;
		this.couponCode = couponCode;
		this.couponPolicy = couponPolicy;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.used = false;
	}

	public boolean isAvailable() {
		return !used;
	}

	/** 사용 처리 */
	public void use() {
		if (used) {
			throw new IllegalStateException("이미 사용된 쿠폰입니다.");
		}
		this.used = true;
	}

	/** 만료 처리 */
	public void expire() {
		this.status = CouponStatus.EXPIRED;
	}

	/** 유효성 검사 */
	public boolean isValid() {
		LocalDateTime now = LocalDateTime.now();
		return status == CouponStatus.UNUSED &&
			now.isAfter(validFrom) &&
			now.isBefore(validTo);
	}

	/** 할인 계산 */
	public int calculateDiscount(int originalPrice) {
		if (!isValid()) {
			throw new IllegalStateException("유효하지 않은 쿠폰입니다.");
		}

		int discount = switch (couponPolicy.getType()) {
			case FIXED -> Math.min(originalPrice, couponPolicy.getDiscountValue());
			case RATE -> originalPrice * couponPolicy.getDiscountValue() / 100;
		};

		if (couponPolicy.getMaxDiscountAmount() != null) {
			discount = Math.min(discount, couponPolicy.getMaxDiscountAmount());
		}

		return discount;
	}

	public CouponPolicy getPolicy() {
		return couponPolicy;
	}
}

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** FK ** -> USER_ID **/
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String couponCode;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer discountAmount;

	@Column(nullable = false)
	private LocalDateTime validFrom;

	@Column(nullable = false)
	private LocalDateTime validTo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CouponStatus status;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public UserCoupon(User user, String couponCode, String name, Integer discountAmount,
		LocalDateTime validFrom, LocalDateTime validTo) {
		this.user = user;
		this.couponCode = couponCode;
		this.name = name;
		this.discountAmount = discountAmount;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.status = CouponStatus.UNUSED;
	}

	public void use() {
		this.status = CouponStatus.USED;
	}

	public void expire() {
		this.status = CouponStatus.EXPIRED;
	}

	public boolean isValid() {
		LocalDateTime now = LocalDateTime.now();
		return status == CouponStatus.UNUSED &&
			now.isAfter(validFrom) &&
			now.isBefore(validTo);
	}

	/**
	 *=====================================================
	 * 					UserCouponDto
	 * =====================================================
	 */

	@Getter
	@NoArgsConstructor
	public static class UserCouponCreateRequest {
		private Long userId;         // 사용자 ID
		private String couponCode;   // 쿠폰 코드
		private String name;         // 쿠폰 이름
		private Integer discountAmount; // 할인 금액
		private LocalDateTime validFrom; // 쿠폰 유효 시작일
		private LocalDateTime validTo;   // 쿠폰 유효 종료일
	}

	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserCouponCreateResponse {

		private Long userCouponId;   // 생성된 쿠폰 ID
		private Long userId;         // 사용자 ID
		private String couponCode;   // 쿠폰 코드
		private String getCouponName;         // 쿠폰 이름
		private Integer discountAmount; // 할인 금액
		private LocalDateTime validFrom; // 쿠폰 유효 시작일
		private LocalDateTime validTo;   // 쿠폰 유효 종료일
		private LocalDateTime createdAt; // 생성일시
	}
} 
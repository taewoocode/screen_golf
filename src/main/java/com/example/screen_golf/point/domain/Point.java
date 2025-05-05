package com.example.screen_golf.point.domain;

import java.time.LocalDateTime;

import com.example.screen_golf.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PointType type;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Point(User user, Integer amount, PointType pointType, LocalDateTime createdAt) {
		this.user = user;
		this.amount = amount;
		this.type = pointType;
		this.createdAt = createdAt;
		validateChargeUnit(amount, pointType);
	}

	private void validateChargeUnit(Integer amount, PointType pointType) {
		if (type == PointType.CHARGE && amount % 5000 != 0) {
			throw new IllegalArgumentException("포인트는 5,000원 단위로만 충전할 수 있습니다.");
		}
	}
}

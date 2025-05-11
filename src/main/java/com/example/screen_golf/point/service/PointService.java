package com.example.screen_golf.point.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.point.domain.Point;
import com.example.screen_golf.point.domain.PointType;
import com.example.screen_golf.point.repository.PointRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointService {
	private final PointRepository pointRepository;
	private final UserRepository userRepository;

	/**
	 * 포인트 사용 가능 여부 확인
	 * @param userId 사용자 ID
	 * @param useAmount 사용하려는 포인트 금액
	 * @return 사용 가능 여부
	 */
	public boolean canUsePoint(Long userId, int useAmount) {
		Integer totalPoint = pointRepository.sumAmountByUserId(userId);
		return totalPoint != null && totalPoint >= useAmount;
	}

	/**
	 * 포인트 사용
	 * @param userId 사용자 ID
	 * @param useAmount 사용할 포인트 금액
	 */
	public void usePoint(Long userId, int useAmount) {
		if (!canUsePoint(userId, useAmount)) {
			throw new IllegalArgumentException("사용 가능한 포인트가 부족합니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		Point point = Point.builder()
			.user(user)
			.amount(-useAmount)  // 사용은 음수로 기록
			.pointType(PointType.USE)
			.createdAt(LocalDateTime.now())
			.build();

		pointRepository.save(point);
		log.info("포인트 사용 완료 - 사용자: {}, 사용 금액: {}", userId, useAmount);
	}

	/**
	 * 포인트 적립 (결제 시 10%)
	 * @param userId 사용자 ID
	 * @param paymentAmount 결제 금액
	 */
	public void accumulatePoint(Long userId, int paymentAmount) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		int pointAmount = (int)(paymentAmount * 0.1);  // 결제 금액의 10%

		Point point = Point.builder()
			.user(user)
			.amount(pointAmount)
			.pointType(PointType.ACCUMULATE)
			.createdAt(LocalDateTime.now())
			.build();

		pointRepository.save(point);
		log.info("포인트 적립 완료 - 사용자: {}, 적립 금액: {}", userId, pointAmount);
	}

	/**
	 * 현재 보유 포인트 조회
	 * @param userId 사용자 ID
	 * @return 보유 포인트
	 */
	public int getCurrentPoint(Long userId) {
		Integer totalPoint = pointRepository.sumAmountByUserId(userId);
		return totalPoint != null ? totalPoint : 0;
	}

	/**
	 * 포인트 내역 조회
	 * @param userId 사용자 ID
	 * @return 포인트 내역
	 */
	public List<Point> getPointHistory(Long userId) {
		return pointRepository.findByUserIdOrderByCreatedAtDesc(userId);
	}
}

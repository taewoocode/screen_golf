package com.example.screen_golf.point.service;

import java.util.List;

import com.example.screen_golf.point.domain.Point;

public interface PointService {
	/**
	 * 포인트 사용 가능 여부 확인
	 * @param userId 사용자 ID
	 * @param useAmount 사용하려는 포인트 금액
	 * @return 사용 가능 여부
	 */
	boolean canUsePoint(Long userId, int useAmount);

	/**
	 * 포인트 사용
	 * @param userId 사용자 ID
	 * @param useAmount 사용할 포인트 금액
	 */
	void usePoint(Long userId, int useAmount);

	/**
	 * 포인트 적립 (결제 시 10%)
	 * @param userId 사용자 ID
	 * @param paymentAmount 결제 금액
	 */
	void accumulatePoint(Long userId, int paymentAmount);

	/**
	 * 현재 보유 포인트 조회
	 * @param userId 사용자 ID
	 * @return 보유 포인트
	 */
	int getCurrentPoint(Long userId);

	/**
	 * 포인트 내역 조회
	 * @param userId 사용자 ID
	 * @return 포인트 내역
	 */
	List<Point> getPointHistory(Long userId);

	/**
	 * 포인트 사용 가능 여부 확인 및 차감 금액 계산
	 * @param userId 사용자 ID
	 * @param usePoint 사용할 포인트
	 * @param originalAmount 원래 금액
	 * @return 차감된 금액
	 */
	Integer validateAndUsePoint(Long userId, Integer usePoint, Integer originalAmount);
}

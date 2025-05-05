package com.example.screen_golf.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.point.domain.Point;
import com.example.screen_golf.point.domain.PointType;
import com.example.screen_golf.point.dto.PointChargeInfo;
import com.example.screen_golf.point.repository.PointRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointService {

	private final PointRepository pointRepository;
	private final UserRepository userRepository;

	@Transactional
	public PointChargeInfo.PointChargeResponse requestPointCharge(PointChargeInfo.PointChargeRequest request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		int chargeAmount = request.getAmount();

		Point point = Point.builder()
			.user(user)
			.amount(chargeAmount)
			.pointType(PointType.CHARGE)
			.build();
		pointRepository.save(point);
		log.info("User {} 충전된 포인트: {}원", user.getId(), chargeAmount);
		return PointChargeInfo.PointChargeResponse.builder()
			.userId(user.getId())
			.chargedAmount(chargeAmount)
			.message("포인트 충전이 완료되었습니다.")
			.build();
	}
}

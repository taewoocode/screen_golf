package com.example.screen_golf.point.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.screen_golf.point.domain.Point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.example.screen_golf.point.domain.PointType;
import com.example.screen_golf.user.domain.User;

@Component
@RequiredArgsConstructor
public class PointConverter {

	public PointInfo.CurrentPointResponse toCurrentPointResponse(int currentPoint) {
		return PointInfo.CurrentPointResponse.builder()
			.currentPoint(currentPoint)
			.build();
	}

	public PointInfo.PointCheckResponse toPointCheckResponse(boolean isAvailable) {
		return PointInfo.PointCheckResponse.builder()
			.isAvailable(isAvailable)
			.build();
	}

	public PointInfo.PointUseResponse toPointUseResponse(Long userId, int useAmount) {
		return PointInfo.PointUseResponse.builder()
			.userId(userId)
			.useAmount(useAmount)
			.usedAt(LocalDateTime.now())
			.build();
	}

	public List<PointInfo.PointHistoryResponse> toPointHistoryResponseList(List<Point> points) {
		return points.stream()
			.map(this::toPointHistoryResponse)
			.collect(Collectors.toList());
	}

	private PointInfo.PointHistoryResponse toPointHistoryResponse(Point point) {
		return PointInfo.PointHistoryResponse.builder()
			.pointId(point.getId())
			.userId(point.getUser().getId())
			.amount(point.getAmount())
			.createdAt(point.getCreatedAt())
			.build();
	}

	public Point makeUsePointEntity(User user, int useAmount) {
		return Point.builder()
			.user(user)
			.amount(-useAmount)
			.pointType(PointType.USE)
			.createdAt(LocalDateTime.now())
			.build();
	}
}

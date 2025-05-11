package com.example.screen_golf.point.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.point.domain.Point;
import com.example.screen_golf.point.dto.PointConverter;
import com.example.screen_golf.point.dto.PointInfo;
import com.example.screen_golf.point.service.PointService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/points")
@Tag(name = "Point", description = "포인트 관련 API")
@RequiredArgsConstructor
public class PointController {

	private final PointService pointService;
	private final PointConverter pointConverter;

	@Operation(summary = SwaggerDocs.SUMMARY_GET_CURRENT_POINT,
		description = SwaggerDocs.DESCRIPTION_GET_CURRENT_POINT)
	@GetMapping("/{userId}/current")
	public ResponseEntity<PointInfo.CurrentPointResponse> getCurrentPoint(@PathVariable Long userId) {
		int currentPoint = pointService.getCurrentPoint(userId);
		return ResponseEntity.ok(pointConverter.toCurrentPointResponse(currentPoint));
	}

	@Operation(summary = SwaggerDocs.SUMMARY_CHECK_POINT_AVAILABILITY,
		description = SwaggerDocs.DESCRIPTION_CHECK_POINT_AVAILABILITY)
	@PostMapping("/{userId}/check")
	public ResponseEntity<PointInfo.PointCheckResponse> checkPointAvailability(
		@PathVariable Long userId,
		@RequestBody PointInfo.PointCheckRequest request
	) {
		boolean isAvailable = pointService.canUsePoint(userId, request.getUseAmount());
		return ResponseEntity.ok(pointConverter.toPointCheckResponse(isAvailable));
	}

	@Operation(summary = SwaggerDocs.SUMMARY_USE_POINT,
		description = SwaggerDocs.DESCRIPTION_USE_POINT)
	@PostMapping("/{userId}/use")
	public ResponseEntity<PointInfo.PointUseResponse> usePoint(
		@PathVariable Long userId,
		@RequestBody PointInfo.PointUseRequest request
	) {
		pointService.usePoint(userId, request.getUseAmount());
		return ResponseEntity.ok(pointConverter.toPointUseResponse(userId, request.getUseAmount()));
	}

	@Operation(summary = SwaggerDocs.SUMMARY_GET_POINT_HISTORY,
		description = SwaggerDocs.DESCRIPTION_GET_POINT_HISTORY)
	@GetMapping("/{userId}/history")
	public ResponseEntity<List<PointInfo.PointHistoryResponse>> getPointHistory(@PathVariable Long userId) {
		List<Point> pointHistory = pointService.getPointHistory(userId);
		return ResponseEntity.ok(pointConverter.toPointHistoryResponseList(pointHistory));
	}
}

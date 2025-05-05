package com.example.screen_golf.point.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.point.dto.PointChargeInfo;
import com.example.screen_golf.point.service.PointService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/point")
@Tag(name = "point", description = "POINT 관련 API")
@Slf4j
@RequiredArgsConstructor
public class PointController {

	private PointService pointService;

	@Operation(
		summary = "포인트 충전",
		description = "포인트를 충전합니다"
	)
	@PostMapping
	public ResponseEntity<PointChargeInfo.PointChargeResponse> requestPointCharge(
		@Parameter(description = "포인트 충전 정보", required = true)
		@RequestBody PointChargeInfo.PointChargeRequest request
	) {
		PointChargeInfo.PointChargeResponse pointChargeResponse
			= pointService.requestPointCharge(request);
		return ResponseEntity.ok(pointChargeResponse);
	}
}

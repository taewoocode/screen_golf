package com.example.screen_golf.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.coupon.service.UserCouponService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user-coupons")
@RequiredArgsConstructor
@Slf4j
public class UserCouponController {

	private final UserCouponService userCouponService;

	@Operation(
		summary = SwaggerDocs.SUMMARY_CREATE_USER_COUPON,
		description = SwaggerDocs.DESCRIPTION_CREATE_USER_COUPON
	)
	@PostMapping
	public ResponseEntity<UserCoupon.UserCouponCreateResponse> createUserCoupon(
		@Parameter(description = "발급할 쿠폰 정보", required = true)
		@RequestBody UserCoupon.UserCouponCreateRequest request
	) {
		UserCoupon.UserCouponCreateResponse response = userCouponService.createCoupon(request);
		log.info("쿠폰 생성 성공 - 쿠폰 이름 = {}, 사용자 ID={}", response.getGetCouponName(), response.getUserId());
		return ResponseEntity.ok(response);
	}
}

package com.example.screen_golf.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.coupon.dto.UserCouponCreateInfo;
import com.example.screen_golf.coupon.dto.UserCouponDeleteInfo;
import com.example.screen_golf.coupon.service.UserCouponService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user-coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coupon", description = "Coupon 관련 API")
public class UserCouponController {

	private final UserCouponService userCouponService;

	@Operation(
		summary = SwaggerDocs.SUMMARY_CREATE_USER_COUPON,
		description = SwaggerDocs.DESCRIPTION_CREATE_USER_COUPON
	)
	@PostMapping
	public ResponseEntity<UserCouponCreateInfo.UserCouponCreateResponse> createUserCoupon(
		@Parameter(description = "발급할 쿠폰 정보", required = true)
		@RequestBody UserCouponCreateInfo.UserCouponCreateRequest request
	) {
		UserCouponCreateInfo.UserCouponCreateResponse couponResponse
			= userCouponService.createCoupon(request);
		log.info("쿠폰 생성 성공 - 쿠폰 이름 = {}, 사용자 ID={}", couponResponse.getGetCouponName(), couponResponse.getUserId());
		return ResponseEntity.ok(couponResponse);
	}

	@Operation(
		summary = SwaggerDocs.SUMMARY_DELETE_USER_COUPON,
		description = SwaggerDocs.DESCRIPTION_DELETE_USER_COUPON
	)
	@DeleteMapping("/{userCouponId}")
	public ResponseEntity<UserCouponDeleteInfo.UserCouponDeleteResponse> deleteUserCoupon(
		@Parameter(name = "userCouponId", description = "삭제할 쿠폰 Id", required = true)
		@PathVariable("userCouponId") Long userCouponId
	) {
		UserCouponDeleteInfo.UserCouponDeleteResponse userCouponDeleteResponse
			= userCouponService.deleteCoupon(userCouponId);
		log.info("쿠폰 삭제 성공 - 쿠폰 이름 = {}", userCouponDeleteResponse.getUserCouponId());
		return ResponseEntity.ok(userCouponDeleteResponse);
	}
}

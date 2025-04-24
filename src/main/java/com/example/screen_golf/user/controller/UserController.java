package com.example.screen_golf.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.swagger.SwaggerDocs;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@Operation(summary = SwaggerDocs.SUMMARY_USER_SIGNUP,
		description = SwaggerDocs.DESCRIPTION_USER_SIGNUP)
	@PostMapping("/signup")
	public ResponseEntity<User.UserSignUpResponse> signUp(@RequestBody User.UserSignUpRequest request) {
		User.UserSignUpResponse response = userService.registerUser(request);
		log.info("회원가입 성공 - 사용자 ID: {}", response.getUserId());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = SwaggerDocs.SUMMARY_USER_INFO,
		description = SwaggerDocs.DESCRIPTION_USER_INFO)
	@PostMapping("/info")
	public ResponseEntity<User.UserInfoResponse> getUserInfo(
		@Parameter(description = "조회할 사용자 ID", required = true)
		@RequestBody User.UserInfoRequest request) {
		User.UserInfoResponse response = userService.findUser(request);
		log.info("회원 정보 조회 성공 - 사용자 ID: {}", response.getUserId());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = SwaggerDocs.SUMMARY_USER_INFO_BY_NAME,
		description = SwaggerDocs.DESCRIPTION_USER_INFO_BY_NAME)
	@PostMapping("/info/name")
	public ResponseEntity<User.UserInfoNameResponse> getUserInfoByName(
		@Parameter(description = "조회할 사용자 이름", required = true)
		@RequestBody User.UserInfoNameRequest request) {
		User.UserInfoNameResponse response = userService.findUser(request);
		log.info("이름으로 회원 정보 조회 성공 - 이름: {}", request.getName());
		return ResponseEntity.ok(response);
	}
} 
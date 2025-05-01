package com.example.screen_golf.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.swagger.SwaggerDocs;
import com.example.screen_golf.user.dto.UserLoginInfo;
import com.example.screen_golf.user.dto.UserLookUpId;
import com.example.screen_golf.user.dto.UserLookUpName;
import com.example.screen_golf.user.dto.UserSignUpInfo;
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
	public ResponseEntity<UserSignUpInfo.UserSignUpResponse> signUp(
		@RequestBody UserSignUpInfo.UserSignUpRequest request) {
		UserSignUpInfo.UserSignUpResponse userSignUpResponse = userService.registerUser(request);
		log.info("회원가입 성공 - 사용자 ID: {}", userSignUpResponse.getUserId());
		return ResponseEntity.ok(userSignUpResponse);
	}

	@Operation(summary = SwaggerDocs.SUMMARY_USER_INFO,
		description = SwaggerDocs.DESCRIPTION_USER_INFO)
	@PostMapping("/info")
	public ResponseEntity<UserLookUpId.UserLookUpIdResponse> getUserInfo(
		@Parameter(description = "조회할 사용자 ID", required = true)
		@RequestBody UserLookUpId.UserLookUpIdRequest request) {
		UserLookUpId.UserLookUpIdResponse response = userService.findUser(request);
		log.info("회원 정보 조회 성공 - 사용자 ID: {}", response.getUserId());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = SwaggerDocs.SUMMARY_USER_INFO_BY_NAME,
		description = SwaggerDocs.DESCRIPTION_USER_INFO_BY_NAME)
	@PostMapping("/info/name")
	public ResponseEntity<UserLookUpName.UserLookUpNameResponse> getUserInfoByName(
		@Parameter(description = "조회할 사용자 이름", required = true)
		@RequestBody UserLookUpName.UserLookUpNameRequest request) {
		UserLookUpName.UserLookUpNameResponse response = userService.findUser(request);
		log.info("이름으로 회원 정보 조회 성공 - 이름: {}", request.getName());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = SwaggerDocs.SUMMARY_USER_INFO_BY_LOGIN,
		description = SwaggerDocs.DESCRIPTION_USER_INFO_BY_LOGIN)
	@PostMapping("/login")
	public ResponseEntity<UserLoginInfo.UserLoginResponse> login(
		@Parameter(description = "로그인", required = true)
		@RequestBody UserLoginInfo.UserLoginRequest request) {
		UserLoginInfo.UserLoginResponse loginUser = userService.login(request);
		log.info("로그인={}", loginUser);
		return ResponseEntity.ok(loginUser);
	}
} 
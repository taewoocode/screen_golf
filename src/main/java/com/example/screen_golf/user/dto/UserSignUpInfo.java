package com.example.screen_golf.user.dto;

import com.example.screen_golf.user.domain.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserSignUpInfo {

	/** ########## 회원가입 DTO ########## **/
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserSignUpRequest {
		private String email;
		private String password;
		private String name;
		private String phone;
		private String profileImage;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserSignUpResponse {
		private Long userId;
		private String email;
		private String name;
		private UserRole role;
		private String token;
	}
}

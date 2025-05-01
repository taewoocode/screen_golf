package com.example.screen_golf.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserLoginInfo {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class UserLoginRequest {
		private String email;
		private String password;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class UserLoginResponse {
		private String userId;
		private String email;
		private String token;
	}

}

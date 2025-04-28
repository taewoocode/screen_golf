package com.example.screen_golf.user.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserSearch {

	/** ########## 회원조회 DTO ########## **/
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfoRequest {
		private Long userId;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserInfoResponse {
		private Long userId;
		private String email;
		private String name;
		private String phone;
		private String profileImage;
		private UserRole role;
		private UserStatus status;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}
}

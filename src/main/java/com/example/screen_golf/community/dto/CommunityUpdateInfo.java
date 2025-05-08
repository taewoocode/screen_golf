package com.example.screen_golf.community.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommunityUpdateInfo {

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Builder
	public static class CommunityUpdateRequest {
		private Long id;
		private String title;
		private String content;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Builder
	public static class CommunityUpdateResponse {
		private Long id;
		private String title;
		private String content;
		private LocalDateTime updateAt;
	}

}

package com.example.screen_golf.community.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.community.domain.PostType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommunitySearchListInfo {

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class CommunitySearchListRequest {
		private String keyword;
	}

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class CommunitySearchListResponse {
		private Long id;
		private String title;  // 제목만 표시
		private PostType postType;  // 게시글 타입
		private String postTypeDesc;  // 게시글 타입 설명
		private LocalDateTime createdAt;  // 작성일
		private Integer commentCount;  // 댓글 수
	}
}

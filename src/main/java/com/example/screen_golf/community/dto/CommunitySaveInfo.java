package com.example.screen_golf.community.dto;

import com.example.screen_golf.community.domain.PostType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class CommunitySaveInfo {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommunitySaveRequest {

		@NotBlank(message = "제목은 필수 입력값입니다.")
		@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
		private String title;

		@NotBlank(message = "내용은 필수 입력값입니다.")
		private String content;

		@NotNull(message = "게시글 구분코드는 필수 입력값입니다.")
		private PostType postType;

		@NotNull(message = "사용자 아이디는 필수 입력값입니다.")
		private Long authorId;

		private String hasAttachment = "N";  // 기본값 N
		private String isBlocked = "N";      // 기본값 N
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommunitySaveResponse {
		private Long id;
		private Integer postNumber;
		private String title;
		private String content;
		private PostType postType;
		private String hasAttachment;
		private String isBlocked;
		private Long authorId;
		private LocalDateTime createdAt;  // 생성 시간
		private LocalDateTime updatedAt;  // 수정 시간
	}
}

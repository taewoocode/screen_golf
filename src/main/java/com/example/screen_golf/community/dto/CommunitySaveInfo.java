package com.example.screen_golf.community.dto;

import java.time.LocalDateTime;

import com.example.screen_golf.community.domain.PostType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class CommunitySaveInfo {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString
	public static class CommunitySaveRequest {

		@NotBlank(message = "제목은 필수 입력값입니다.")
		@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
		@Schema(description = "게시글 제목", example = "테스트 게시글")
		private String title;

		@NotBlank(message = "내용은 필수 입력값입니다.")
		@Schema(description = "게시글 내용", example = "테스트 내용입니다.")
		private String content;

		@NotNull(message = "게시글 구분코드는 필수 입력값입니다.")
		@Schema(description = "게시글 타입", example = "NOTICE")
		private PostType postType;

		@Schema(description = "첨부파일 여부", example = "N", defaultValue = "N")
		private String hasAttachment = "N";  // 기본값 N

		@Schema(description = "차단 여부", example = "N", defaultValue = "N")
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

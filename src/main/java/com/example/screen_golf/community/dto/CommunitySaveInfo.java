package com.example.screen_golf.community.dto;

import com.example.screen_golf.community.domain.PostType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommunitySaveInfo {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommunitySaveRequest {

		@NotBlank(message = "제목은 필수 입력값입니다.")
		@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
		private String postTil;

		@NotBlank(message = "내용은 필수 입력값입니다.")
		private String postCnts;

		@NotNull(message = "게시글 구분코드는 필수 입력값입니다.")
		private PostType postDvCd;

		@NotNull(message = "사용자 아이디는 필수 입력값입니다.")
		private Long userId;

		private String atcfYn = "N";  // 기본값 N
		private String stopYn = "N";  // 기본값 N
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommunitySaveResponse {
		private Long id;
		private Integer postNo;
		private String postTil;
		private String postCnts;
		private PostType postDvCd;
		private String atcfYn;
		private String stopYn;
		private Long userId;
	}
}

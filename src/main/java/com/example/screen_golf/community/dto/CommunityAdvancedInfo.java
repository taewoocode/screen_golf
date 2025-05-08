package com.example.screen_golf.community.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.screen_golf.community.domain.PostType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommunityAdvancedInfo {

	/**
	 * 고급 검색 요청 DTO
	 * - 키워드 검색
	 * - 게시글 타입 필터링
	 * - 날짜 범위 검색
	 * - 페이징 처리
	 */
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class CommunityAdvancedSearchRequest {
		private String keyword;           // 검색 키워드
		private PostType postType;        // 게시글 타입 필터
		private LocalDateTime startDate;  // 검색 시작일
		private LocalDateTime endDate;    // 검색 종료일
		private Integer page;             // 페이지 번호 (0부터 시작)
		private Integer size;             // 페이지 크기
		private String sortBy;            // 정렬 기준 (createdAt, title 등)
		private String sortDirection;     // 정렬 방향 (asc, desc)
	}

	/**
	 * 고급 검색 응답 DTO
	 * - 검색 결과
	 * - 페이징 정보
	 * - 정렬 정보
	 */
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class CommunityAdvancedSearchResponse {
		private List<CommunityItem> communities;  // 검색 결과 목록
		private Integer totalElements;    // 전체 검색 결과 수
		private Integer totalPages;       // 전체 페이지 수
		private Integer currentPage;      // 현재 페이지
		private Integer pageSize;         // 페이지 크기
		private Boolean hasNext;          // 다음 페이지 존재 여부
		private Boolean hasPrevious;      // 이전 페이지 존재 여부

		@Builder
		@AllArgsConstructor
		@NoArgsConstructor
		@Getter
		public static class CommunityItem {
			private Long id;                  // 게시글 ID
			private Integer postNumber;       // 게시글 번호
			private String title;             // 제목
			private String content;           // 내용
			private PostType postType;        // 게시글 타입
			private String postTypeDesc;      // 게시글 타입 설명
			private String hasAttachment;     // 첨부파일 여부
			private String isBlocked;         // 차단 여부
			private Long authorId;            // 작성자 ID
			private LocalDateTime createdAt;  // 작성일
			private LocalDateTime updatedAt;  // 수정일
			private Integer commentCount;     // 댓글 수
		}
	}
}

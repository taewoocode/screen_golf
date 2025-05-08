package com.example.screen_golf.community.dto;

import java.util.List;

import com.example.screen_golf.community.domain.Community;

import lombok.Builder;
import lombok.Getter;

public class CommunityConverter {

	/**
	 * Community -> CommunitySaveInfo -> Save 반환로직
	 * @param community
	 * @return
	 */
	public static CommunitySaveInfo.CommunitySaveResponse toResponse(Community community) {
		return CommunitySaveInfo.CommunitySaveResponse.builder()
			.id(community.getId())
			.postNumber(community.getPostNumber())
			.title(community.getTitle())
			.content(community.getContent())
			.postType(community.getPostType())
			.hasAttachment(community.getHasAttachment())
			.isBlocked(community.getIsBlocked())
			.authorId(community.getAuthorId())
			.createdAt(community.getCreatedAt())
			.updatedAt(community.getUpdatedAt())
			.build();
	}

	/**
	 * CommunitySaveRequest -> Community로 변환 -> Save 로직
	 * @param request
	 * @return
	 */
	public static Community toEntity(CommunitySaveInfo.CommunitySaveRequest request) {
		return Community.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.postType(request.getPostType())
			.authorId(request.getAuthorId())
			.hasAttachment(request.getHasAttachment())
			.isBlocked(request.getIsBlocked())
			.build();
	}

	/**
	 * // Community -> CommunitySearchListResponse 변환
	 * @param community
	 * @param commentCount
	 * @return
	 */
	public static CommunitySearchListInfo.CommunitySearchListResponse toCommunitySearchListResponse(
		Community community, int commentCount) {
		return CommunitySearchListInfo.CommunitySearchListResponse.builder()
			.id(community.getId())
			.title(community.getTitle())  // 제목만 반환
			.postType(community.getPostType())
			.postTypeDesc(community.getPostType().getDescription())  // 게시글 타입 설명 추가
			.createdAt(community.getCreatedAt())  // 작성일
			.commentCount(commentCount)  // 댓글 수
			.build();
	}

	public static CommunityUpdateInfo.CommunityUpdateResponse toUpdateResponse(Community community) {
		return CommunityUpdateInfo.CommunityUpdateResponse.builder()
			.id(community.getId())
			.title(community.getTitle())
			.content(community.getContent())
			.updateAt(community.getUpdatedAt())
			.build();
	}

	/**
	 * Community -> CommunityAdvancedSearchResponse.CommunityItem 변환
	 */
	public static CommunityAdvancedInfo.CommunityAdvancedSearchResponse.CommunityItem toAdvancedSearchResponse(
		Community community, int commentCount) {
		return CommunityAdvancedInfo.CommunityAdvancedSearchResponse.CommunityItem.builder()
			.id(community.getId())
			.postNumber(community.getPostNumber())
			.title(community.getTitle())
			.content(community.getContent())
			.postType(community.getPostType())
			.postTypeDesc(community.getPostType().getDescription())
			.hasAttachment(community.getHasAttachment())
			.isBlocked(community.getIsBlocked())
			.authorId(community.getAuthorId())
			.createdAt(community.getCreatedAt())
			.updatedAt(community.getUpdatedAt())
			.commentCount(commentCount)
			.build();
	}

	/**
	 * 페이징 정보를 담는 클래스
	 */
	@Builder
	@Getter
	public static class PagingInfo {
		private final int start;
		private final int end;
		private final int totalSize;
		private final int pageSize;
		private final int currentPage;

		public boolean hasNext() {
			return end < totalSize;
		}

		public boolean hasPrevious() {
			return start > 0;
		}

		public int getTotalPages() {
			return (int) Math.ceil((double) totalSize / pageSize);
		}
	}

	/**
	 * 페이징 정보 생성
	 */
	public static PagingInfo createPagingInfo(int start, int end, int totalSize, int pageSize, int currentPage) {
		return PagingInfo.builder()
			.start(start)
			.end(end)
			.totalSize(totalSize)
			.pageSize(pageSize)
			.currentPage(currentPage)
			.build();
	}

	/**
	 * 고급 검색 응답 생성
	 */
	public static CommunityAdvancedInfo.CommunityAdvancedSearchResponse toMakeAdvancedResponse(
		List<CommunityAdvancedInfo.CommunityAdvancedSearchResponse.CommunityItem> items,
		PagingInfo pagingInfo) {
		
		return CommunityAdvancedInfo.CommunityAdvancedSearchResponse.builder()
			.communities(items)
			.totalElements(pagingInfo.getTotalSize())
			.totalPages(pagingInfo.getTotalPages())
			.currentPage(pagingInfo.getCurrentPage())
			.pageSize(pagingInfo.getPageSize())
			.hasNext(pagingInfo.hasNext())
			.hasPrevious(pagingInfo.hasPrevious())
			.build();
	}
}

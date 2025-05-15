package com.example.screen_golf.community.dto;

import java.util.List;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.domain.CommunityDocument;
import com.example.screen_golf.community.domain.PostType;
import com.example.screen_golf.utils.SecurityUtil;

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
	 * CommunitySaveRequest -> Community로 변환 (postNumber 포함)
	 * @param request
	 * @param postNumber
	 * @return
	 */
	public static Community toMakeCommunitySaveEntity(CommunitySaveInfo.CommunitySaveRequest request,
		Integer postNumber) {
		return Community.builder()
			.postNumber(postNumber)
			.title(request.getTitle())
			.content(request.getContent())
			.postType(request.getPostType())
			.authorId(SecurityUtil.getCurrentUserId())
			.hasAttachment(request.getHasAttachment() != null ? request.getHasAttachment() : "N")
			.isBlocked(request.getIsBlocked() != null ? request.getIsBlocked() : "N")
			.replyNumber(0)
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
			return (int)Math.ceil((double)totalSize / pageSize);
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

	public static CommunityDocument toDocument(Community entity) {
		return CommunityDocument.builder()
			.id(String.valueOf(entity.getId()))
			.postNumber(entity.getPostNumber())
			.replyNumber(entity.getReplyNumber())
			.parentReplyNumber(entity.getParentReplyNumber())
			.title(entity.getTitle())
			.content(entity.getContent())
			.postType(entity.getPostType().name())
			.hasAttachment(entity.getHasAttachment())
			.isBlocked(entity.getIsBlocked())
			.authorId(entity.getAuthorId())
			.createdAt(entity.getCreatedAt().toLocalDate())
			.updatedAt(entity.getUpdatedAt().toLocalDate())
			.build();
	}

	/**
	 * CommunityDocument를 Community로 변환
	 */
	public static Community toEntity(CommunityDocument document) {
		return Community.builder()
			.postNumber(document.getPostNumber())
			.replyNumber(document.getReplyNumber())
			.parentReplyNumber(document.getParentReplyNumber())
			.title(document.getTitle())
			.content(document.getContent())
			.postType(PostType.valueOf(document.getPostType()))
			.hasAttachment(document.getHasAttachment())
			.isBlocked(document.getIsBlocked())
			.authorId(document.getAuthorId())
			.build();
	}

}

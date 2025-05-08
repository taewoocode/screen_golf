package com.example.screen_golf.community.dto;

import com.example.screen_golf.community.domain.Community;

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
}

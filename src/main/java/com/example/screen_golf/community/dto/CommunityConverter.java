package com.example.screen_golf.community.dto;

import com.example.screen_golf.community.domain.Community;

public class CommunityConverter {

	public static CommunitySaveInfo.CommunitySaveResponse toResponse(Community community) {
		return CommunitySaveInfo.CommunitySaveResponse.builder()
			.id(community.getId())
			.postNo(community.getPostNo())
			.postTil(community.getPostTil())
			.postCnts(community.getPostCnts())
			.postDvCd(community.getPostDvCd())
			.atcfYn(community.getAtcfYn())
			.stopYn(community.getStopYn())
			.userId(community.getUserId())
			.build();
	}

	public static Community toEntity(CommunitySaveInfo.CommunitySaveRequest request) {
		return Community.builder()
			.postTil(request.getPostTil())
			.postCnts(request.getPostCnts())
			.postDvCd(request.getPostDvCd())
			.userId(request.getUserId())
			.atcfYn(request.getAtcfYn())
			.stopYn(request.getStopYn())
			.build();
	}
}

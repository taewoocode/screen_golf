package com.example.screen_golf.community.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.dto.CommunitySearchListInfo;

public interface CommunityService {

	@Transactional
	CommunitySaveInfo.CommunitySaveResponse savePost(CommunitySaveInfo.CommunitySaveRequest request);

	@Transactional(readOnly = true)
	List<CommunitySearchListInfo.CommunitySearchListResponse> findCommunityList(
		CommunitySearchListInfo.CommunitySearchListRequest request);

	// 삭제 메서드 추가
	@Transactional
	void deletePost(Long id);
}

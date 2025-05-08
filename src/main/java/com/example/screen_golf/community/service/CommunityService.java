package com.example.screen_golf.community.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.community.dto.CommunitySaveInfo;

public interface CommunityService {

	@Transactional
	CommunitySaveInfo.CommunitySaveResponse savePost(CommunitySaveInfo.CommunitySaveRequest request);
}

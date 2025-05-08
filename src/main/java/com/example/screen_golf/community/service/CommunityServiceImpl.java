package com.example.screen_golf.community.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.dto.CommunityConverter;
import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.repository.elasticsearch.CommunitySearchRepository;
import com.example.screen_golf.community.repository.jpa.CommunityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

	private final CommunityRepository communityRepository;
	private final CommunitySearchRepository communitySearchRepository;

	/**
	 *
	 * @param request
	 * @return
	 */
	@Override
	@Transactional
	public CommunitySaveInfo.CommunitySaveResponse savePost(CommunitySaveInfo.CommunitySaveRequest request) {
		Community entity = CommunityConverter.toEntity(request);
		Community savedPost = communityRepository.save(entity);
		return CommunityConverter.toResponse(savedPost);
	}
}

package com.example.screen_golf.community.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.dto.CommunityConverter;
import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.dto.CommunitySearchListInfo;
import com.example.screen_golf.community.repository.elasticsearch.CommunityElasticSearchRepository;
import com.example.screen_golf.community.repository.jpa.CommunityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

	private final CommunityRepository communityRepository;
	private final CommunityElasticSearchRepository communityElasticSearchRepository;

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

	/**
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<CommunitySearchListInfo.CommunitySearchListResponse> findCommunityList(
		CommunitySearchListInfo.CommunitySearchListRequest request) {
		List<Community> communities = communityElasticSearchRepository.findByKeyword(request.getKeyword());
		return communities.stream()
			.map(CommunityConverter::toCommunitySearchListResponse)
			.collect(Collectors.toList());
	}

	// 댓글 수 조회
	private int getCommentCountForCommunity(Community community) {
		return communityRepository.countByParentReplyNumber(community.getPostNumber());
	}
}

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
	 * 게시글 저장
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
	 * 키워드로 게시글을 조회할 때 댓글 수를 함께 반환
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<CommunitySearchListInfo.CommunitySearchListResponse> findCommunityList(
		CommunitySearchListInfo.CommunitySearchListRequest request) {
		List<Community> communities = communityElasticSearchRepository.findByKeyword(request.getKeyword());
		return communities.stream()
			.map(community -> {
				int commentCount = getCommentCountForCommunity(community);
				return CommunityConverter.toCommunitySearchListResponse(community, commentCount);
			})
			.collect(Collectors.toList());
	}

	/**
	 * 게시글 삭제 추가
	 * @param id
	 */
	@Override
	@Transactional
	public void deletePost(Long id) {
		try {
			// 해당 Community 객체가 존재하는지 확인
			if (communityRepository.existsById(id)) {
				// Community 객체 삭제
				communityRepository.deleteById(id);
				log.info("게시글이 삭제되었습니다={}", id);  // 한글로 변경
			} else {
				log.warn("게시글을 찾을 수 없습니다={}", id);  // 한글로 변경
			}
		} catch (Exception e) {
			log.error("게시글 삭제 중 오류 발생={}", e.getMessage());  // 한글로 변경
			throw new RuntimeException("게시글 삭제 실패.");
		}
	}

	/**
	 * 댓글 수 조회
	 * @param community
	 * @return
	 */
	private int getCommentCountForCommunity(Community community) {
		return communityRepository.countByParentReplyNumber(community.getPostNumber());
	}
}

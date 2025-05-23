package com.example.screen_golf.community.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.domain.CommunityDocument;
import com.example.screen_golf.community.dto.CommunityAdvancedInfo;
import com.example.screen_golf.community.dto.CommunityConverter;
import com.example.screen_golf.community.dto.CommunityFuzzySearchInfo;
import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.dto.CommunitySearchListInfo;
import com.example.screen_golf.community.dto.CommunityUpdateInfo;
import com.example.screen_golf.community.repository.elasticsearch.CommunityElasticSearchRepository;
import com.example.screen_golf.community.repository.jpa.CommunityRepository;
import com.example.screen_golf.exception.community.CommunityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

	private final CommunityRepository communityRepository;
	private final CommunityElasticSearchRepository communityElasticSearchRepository;

	@Override
	@Transactional
	public CommunitySaveInfo.CommunitySaveResponse savePost(CommunitySaveInfo.CommunitySaveRequest request) {
		Integer maxPostNumber = communityRepository.findAll().stream()
			.map(Community::getPostNumber)
			.max(Integer::compareTo)
			.orElse(0);
		Community entity = CommunityConverter.toMakeCommunitySaveEntity(request, maxPostNumber + 1);
		Community savedPost = communityRepository.save(entity);

		CommunityDocument document = CommunityConverter.toDocument(savedPost);
		communityElasticSearchRepository.save(document);

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
			if (communityRepository.existsById(id)) {
				communityRepository.deleteById(id);
				log.info("게시글이 삭제되었습니다={}", id);
			} else {
				log.warn("게시글을 찾을 수 없습니다={}", id);
			}
		} catch (Exception e) {
			log.error("게시글 삭제 중 오류 발생={}", e.getMessage());
			throw new RuntimeException("게시글 삭제 실패.");
		}
	}

	@Override
	@Transactional
	public CommunityUpdateInfo.CommunityUpdateResponse updatePost(CommunityUpdateInfo.CommunityUpdateRequest request) {
		Community community = communityRepository.findById(request.getId())
			.orElseThrow(() -> new CommunityNotFoundException("해당 게시글을 찾을 수 없습니다."));
		community.update(request.getTitle(), request.getContent());
		return CommunityConverter.toUpdateResponse(community);
	}

	/**
	 * 댓글 수 조회
	 * @param community
	 * @return
	 */
	protected int getCommentCountForCommunity(Community community) {
		return communityRepository.countByParentReplyNumber(community.getPostNumber());
	}

	@Override
	@Transactional(readOnly = true)
	public CommunityAdvancedInfo.CommunityAdvancedSearchResponse advancedSearch(
		CommunityAdvancedInfo.CommunityAdvancedSearchRequest request) {

		Sort sort = Sort.by(
			Sort.Direction.fromString(request.getSortDirection()),
			request.getSortBy()
		);
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		List<Community> communities = communityElasticSearchRepository.findByKeywordAndPostType(
			request.getKeyword(),
			request.getPostType().name()
		);

		if (request.getStartDate() != null && request.getEndDate() != null) {
			communities = communities.stream()
				.filter(community ->
					!community.getCreatedAt().isBefore(request.getStartDate()) &&
						!community.getCreatedAt().isAfter(request.getEndDate()))
				.collect(Collectors.toList());
		}

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), communities.size());
		List<Community> pagedCommunities = communities.subList(start, end);

		List<CommunityAdvancedInfo.CommunityAdvancedSearchResponse.CommunityItem> responses = pagedCommunities.stream()
			.map(community -> {
				int commentCount = communityRepository.countByParentReplyNumber(community.getPostNumber());
				return CommunityConverter.toAdvancedSearchResponse(community, commentCount);
			})
			.collect(Collectors.toList());

		CommunityConverter.PagingInfo pagingInfo = CommunityConverter.createPagingInfo(
			start, end, communities.size(), request.getSize(), request.getPage());

		return CommunityConverter.toMakeAdvancedResponse(responses, pagingInfo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommunityFuzzySearchInfo.CommunityFuzzySearchResponse> fuzzySearch(
		CommunityFuzzySearchInfo.CommunityFuzzySearchRequest request) {
		List<CommunityDocument> documents = communityElasticSearchRepository.findByKeywordFuzzy(request.getKeyword());
		List<Community> communities = documents.stream()
			.map(CommunityConverter::toEntity)
			.collect(Collectors.toList());

		return communities.stream()
			.map(community -> {
				int commentCount = getCommentCountForCommunity(community);
				return CommunityFuzzySearchInfo.CommunityFuzzySearchResponse.builder()
					.id(community.getId())
					.title(community.getTitle())
					.content(community.getContent())
					.postType(community.getPostType().name())
					.postTypeDesc(community.getPostType().getDescription())
					.commentCount(commentCount)
					.build();
			})
			.collect(Collectors.toList());
	}

	/**
	 * 기존 데이터를 Elasticsearch에 재인덱싱
	 */
	@Transactional
	public void reindexAllData() {
		List<Community> allCommunities = communityRepository.findAll();
		List<CommunityDocument> documents = allCommunities.stream()
			.map(CommunityConverter::toDocument)
			.collect(Collectors.toList());

		communityElasticSearchRepository.saveAll(documents);
	}
}

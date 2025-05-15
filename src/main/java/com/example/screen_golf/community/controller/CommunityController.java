package com.example.screen_golf.community.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.community.dto.CommunityAdvancedInfo;
import com.example.screen_golf.community.dto.CommunityFuzzySearchInfo;
import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.dto.CommunitySearchListInfo;
import com.example.screen_golf.community.dto.CommunityUpdateInfo;
import com.example.screen_golf.community.service.CommunityService;
import com.example.screen_golf.swagger.SwaggerDocs;
import com.example.screen_golf.utils.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 관련 API")
public class CommunityController {

	private final CommunityService communityService;

	/**
	 * 게시글 작성
	 * @param request CommunitySaveRequest DTO
	 * @return CommunitySaveResponse DTO
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_REQUEST_COMMUNITY,
		description = SwaggerDocs.DESCRIPTION_REQUEST_COMMUNITY
	)
	@PostMapping
	public ResponseEntity<CommunitySaveInfo.CommunitySaveResponse> createPost(
		@Valid @RequestBody CommunitySaveInfo.CommunitySaveRequest request
	) {
		Long currentUserId = SecurityUtil.getCurrentUserId();
		log.info("게시글 작성 요청 - 제목: {}, 작성자 ID: {}", request.getTitle(), currentUserId);
		CommunitySaveInfo.CommunitySaveResponse response = communityService.savePost(request);
		log.info("게시글 작성 완료 - 게시글 ID: {}", response.getId());
		return ResponseEntity.ok(response);
	}

	/**
	 * 게시글 수가
	 * @param request
	 * @return
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_UPDATE_COMMUNITY,
		description = SwaggerDocs.DESCRIPTION_UPDATE_COMMUNITY
	)
	public ResponseEntity<CommunityUpdateInfo.CommunityUpdateResponse> updatePost(
		@RequestBody CommunityUpdateInfo.CommunityUpdateRequest request
	) {
		CommunityUpdateInfo.CommunityUpdateResponse communityUpdateResponse =
			communityService.updatePost(request);
		return ResponseEntity.ok(communityUpdateResponse);
	}

	/**
	 * 게시글 삭제
	 * @param id
	 * @return
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_DELETE_COMMUNITY,
		description = SwaggerDocs.DESCRIPTION_DELETE_COMMUNITY
	)
	public ResponseEntity<Void> deletePost(
		@PathVariable Long id
	) {
		communityService.deletePost(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 키워드로 게시글 목록을 조회
	 * @param request 검색 조건을 담은 요청 객체
	 * @return 게시글 목록
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_FIND_KEYWORD_COMMUNITY,
		description = SwaggerDocs.DESCRIPTION_FIND_KEYWORD_COMMUNITY
	)
	@PostMapping("/search")  // HTTP POST 메서드로 검색
	public ResponseEntity<List<CommunitySearchListInfo.CommunitySearchListResponse>> findCommunityList(
		@RequestBody CommunitySearchListInfo.CommunitySearchListRequest request
	) {
		List<CommunitySearchListInfo.CommunitySearchListResponse> communityList =
			communityService.findCommunityList(request);  // 서비스에서 데이터 조회
		return ResponseEntity.ok(communityList);  // 조회된 목록을 200 OK로 반환
	}

	/**
	 * 고급 검색 기능
	 * @param request
	 * @return
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ADVANCED_SEARCH_COMMUNITY,
		description = SwaggerDocs.DESCRIPTION_ADVANCED_SEARCH_COMMUNITY
	)
	@PostMapping("/search/advanced")
	public ResponseEntity<CommunityAdvancedInfo.CommunityAdvancedSearchResponse> advancedSearch(
		@Parameter(description = "검색 조건", required = true)
		@RequestBody CommunityAdvancedInfo.CommunityAdvancedSearchRequest request
	) {
		log.info("고급 검색 요청: {}", request);
		CommunityAdvancedInfo.CommunityAdvancedSearchResponse response = communityService.advancedSearch(request);
		return ResponseEntity.ok(response);
	}

	/**
	 * 퍼지 검색 기능
	 * - 오타 허용 검색
	 * - 자동 퍼지니스 설정
	 * - 접두어 2글자 이상 일치 필요
	 */
	@Operation(
		summary = "퍼지 검색",
		description = "오타를 허용하는 퍼지 검색 기능을 제공합니다."
	)
	@PostMapping("/search/fuzzy")
	public ResponseEntity<List<CommunityFuzzySearchInfo.CommunityFuzzySearchResponse>> fuzzySearch(
		@RequestBody CommunityFuzzySearchInfo.CommunityFuzzySearchRequest request
	) {
		List<CommunityFuzzySearchInfo.CommunityFuzzySearchResponse> results = communityService.fuzzySearch(request);
		return ResponseEntity.ok(results);
	}

	/**
	 * Elasticsearch 재인덱싱
	 */
	@Operation(
		summary = "Elasticsearch 재인덱싱",
		description = "기존 데이터를 Elasticsearch에 재인덱싱합니다."
	)
	@PostMapping("/reindex")
	public ResponseEntity<Void> reindexData() {
		communityService.reindexAllData();
		return ResponseEntity.ok().build();
	}
}

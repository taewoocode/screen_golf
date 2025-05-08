package com.example.screen_golf.community.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.dto.CommunitySearchListInfo;
import com.example.screen_golf.community.dto.CommunityUpdateInfo;
import com.example.screen_golf.community.service.CommunityService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "Community", description = "Community 관련 API")
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
		@RequestBody CommunitySaveInfo.CommunitySaveRequest request) {
		log.info("게시글 작성 요청: {}", request);
		CommunitySaveInfo.CommunitySaveResponse response = communityService.savePost(request);
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

}

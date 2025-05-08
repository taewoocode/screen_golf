package com.example.screen_golf.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.community.dto.CommunitySaveInfo;
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
}

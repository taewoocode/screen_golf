package com.example.screen_golf.community.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.community.dto.CommunityAdvancedInfo;
import com.example.screen_golf.community.dto.CommunityFuzzySearchInfo;
import com.example.screen_golf.community.dto.CommunitySaveInfo;
import com.example.screen_golf.community.dto.CommunitySearchListInfo;
import com.example.screen_golf.community.dto.CommunityUpdateInfo;

public interface CommunityService {

	@Transactional
	CommunitySaveInfo.CommunitySaveResponse savePost(CommunitySaveInfo.CommunitySaveRequest request);

	@Transactional(readOnly = true)
	List<CommunitySearchListInfo.CommunitySearchListResponse> findCommunityList(
		CommunitySearchListInfo.CommunitySearchListRequest request);

	// 삭제 메서드 추가
	@Transactional
	void deletePost(Long id);

	/**
	 * Update -> 게시글 id, 제목, 내용을 수정
	 */
	@Transactional
	CommunityUpdateInfo.CommunityUpdateResponse updatePost(CommunityUpdateInfo.CommunityUpdateRequest request);

	/**
	 * 고급 검색 기능
	 * - 키워드 검색
	 * - 게시글 타입 필터링
	 * - 날짜 범위 검색
	 * - 페이징 처리
	 * - 정렬 기능
	 */
	@Transactional(readOnly = true)
	CommunityAdvancedInfo.CommunityAdvancedSearchResponse advancedSearch(
		CommunityAdvancedInfo.CommunityAdvancedSearchRequest request);

	/**
	 * 퍼지 검색 기능
	 * - 오타 허용 검색
	 * - 자동 퍼지니스 설정
	 * - 접두어 2글자 이상 일치 필요
	 */
	@Transactional(readOnly = true)
	List<CommunityFuzzySearchInfo.CommunityFuzzySearchResponse> fuzzySearch(
		CommunityFuzzySearchInfo.CommunityFuzzySearchRequest request);
}

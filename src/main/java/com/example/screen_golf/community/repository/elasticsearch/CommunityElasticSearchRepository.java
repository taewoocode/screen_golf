package com.example.screen_golf.community.repository.elasticsearch;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.domain.CommunityDocument;

public interface CommunityElasticSearchRepository
	extends ElasticsearchRepository<CommunityDocument, String>, CustomCommunitySearchRepository {
	// 기본 CRUD 메서드들은 ElasticsearchRepository에서 제공

	/**
	 * 기본 키워드 검색
	 * - 제목과 내용에서 동시에 검색
	 * - 기본적인 전문 검색 기능 제공
	 */
	List<Community> findByKeyword(String keyword);

	/**
	 * 고급 키워드 검색 + 필터링
	 * - 제목에 2배 가중치 부여 (title^2)
	 * - 게시글 타입과 차단 여부로 필터링
	 * - 생성일 기준 내림차순 정렬
	 */
	List<Community> findByKeywordAndPostType(String keyword, String postType);

	/**
	 * 날짜 범위 검색 + 페이징
	 * - 특정 기간 내 게시글 검색
	 * - 페이징 처리 지원
	 * - 생성일 기준 내림차순 정렬
	 */
	List<Community> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, int from, int size);

	/**
	 * 복합 검색 (제목/내용 + 작성자)
	 * - 제목에 3배, 내용에 2배 가중치 부여
	 * - 작성자 ID로도 검색 가능
	 * - should 조건으로 OR 검색 지원
	 */
	List<Community> findByKeywordOrAuthor(String keyword, Long authorId);

	/**
	 * 유사도 검색 (Fuzzy search)
	 * - 오타 허용 검색
	 * - 짧은 초성도 매칭
	 */
	List<CommunityDocument> findByKeywordFuzzy(String keyword);

	/**
	 * 하이라이팅 검색
	 * - 검색어 하이라이팅 지원
	 * - 내용은 150자 단위로 3개 조각 반환
	 * - SearchHits로 하이라이팅 정보 포함
	 */
	SearchHits<Community> findByKeywordWithHighlight(String keyword);

	/**
	 * 통계 집계 검색
	 * - 게시글 타입별 집계
	 * - 일별 게시글 수 집계
	 * - SearchHits로 집계 결과 포함
	 */
	SearchHits<Community> getPostStatistics();
}


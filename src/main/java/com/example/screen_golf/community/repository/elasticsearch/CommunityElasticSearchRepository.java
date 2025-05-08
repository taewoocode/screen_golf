package com.example.screen_golf.community.repository.elasticsearch;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.screen_golf.community.domain.Community;

public interface CommunityElasticSearchRepository extends ElasticsearchRepository<Community, Long> {

	/**
	 * 기본 키워드 검색
	 * - 제목과 내용에서 동시에 검색
	 * - 기본적인 전문 검색 기능 제공
	 */
	@Query("""
		{
			"query": {
				"multi_match": {
					"query": "?0",
					"fields": ["title", "content"]
				}
			}
		}
	""")
	List<Community> findByKeyword(String keyword);

	/**
	 * 고급 키워드 검색 + 필터링
	 * - 제목에 2배 가중치 부여 (title^2)
	 * - 게시글 타입과 차단 여부로 필터링
	 * - 생성일 기준 내림차순 정렬
	 */
	@Query("""
		{
			"query": {
				"bool": {
					"must": [
						{
							"multi_match": {
								"query": "?0",
								"fields": ["title^2", "content"],
								"type": "best_fields",
								"operator": "and"
							}
						}
					],
					"filter": [
						{ "term": { "postType": "?1" } },
						{ "term": { "isBlocked": "N" } }
					]
				}
			},
			"sort": [
				{ "createdAt": "desc" }
			]
		}
	""")
	List<Community> findByKeywordAndPostType(String keyword, String postType);

	/**
	 * 날짜 범위 검색 + 페이징
	 * - 특정 기간 내 게시글 검색
	 * - 페이징 처리 지원
	 * - 생성일 기준 내림차순 정렬
	 */
	@Query("""
		{
			"query": {
				"bool": {
					"must": [
						{
							"range": {
								"createdAt": {
									"gte": "?0",
									"lte": "?1"
								}
							}
						}
					]
				}
			},
			"sort": [
				{ "createdAt": "desc" }
			],
			"from": ?2,
			"size": ?3
		}
	""")
	List<Community> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, int from, int size);

	/**
	 * 복합 검색 (제목/내용 + 작성자)
	 * - 제목에 3배, 내용에 2배 가중치 부여
	 * - 작성자 ID로도 검색 가능
	 * - should 조건으로 OR 검색 지원
	 */
	@Query("""
		{
			"query": {
				"bool": {
					"should": [
						{
							"multi_match": {
								"query": "?0",
								"fields": ["title^3", "content^2"],
								"type": "best_fields"
							}
						},
						{
							"match": {
								"authorId": "?1"
							}
						}
					],
					"minimum_should_match": 1
				}
			}
		}
	""")
	List<Community> findByKeywordOrAuthor(String keyword, Long authorId);

	/**
	 * 유사도 검색 (Fuzzy search)
	 * - 오타 허용 검색
	 * - 자동 퍼지니스 설정
	 * - 접두어 2글자 이상 일치 필요
	 */
	@Query("""
		{
			"query": {
				"multi_match": {
					"query": "?0",
					"fields": ["title", "content"],
					"fuzziness": "AUTO",
					"prefix_length": 2
				}
			}
		}
	""")
	List<Community> findByKeywordFuzzy(String keyword);

	/**
	 * 하이라이팅 검색
	 * - 검색어 하이라이팅 지원
	 * - 내용은 150자 단위로 3개 조각 반환
	 * - SearchHits로 하이라이팅 정보 포함
	 */
	@Query("""
		{
			"query": {
				"multi_match": {
					"query": "?0",
					"fields": ["title", "content"]
				}
			},
			"highlight": {
				"fields": {
					"title": {},
					"content": {
						"fragment_size": 150,
						"number_of_fragments": 3
					}
				}
			}
		}
	""")
	SearchHits<Community> findByKeywordWithHighlight(String keyword);

	/**
	 * 통계 집계 검색
	 * - 게시글 타입별 집계
	 * - 일별 게시글 수 집계
	 * - SearchHits로 집계 결과 포함
	 */
	@Query("""
		{
			"size": 0,
			"aggs": {
				"post_types": {
					"terms": {
						"field": "postType"
					}
				},
				"daily_posts": {
					"date_histogram": {
						"field": "createdAt",
						"calendar_interval": "day"
					}
				}
			}
		}
	""")
	SearchHits<Community> getPostStatistics();
}

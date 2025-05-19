package com.example.screen_golf.community.repository.elasticsearch;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.domain.CommunityDocument;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomCommunitySearchRepositoryImpl implements CustomCommunitySearchRepository {

	private final ElasticsearchOperations operations;

	@Override
	public List<Community> findByKeyword(String keyword) {
		String queryString = String.format("""
			{
				"query": {
					"multi_match": {
						"query": "%s",
						"fields": ["title", "content"]
					}
				}
			}
			""", keyword);

		Query query = new StringQuery(queryString);
		SearchHits<Community> searchHits = operations.search(query, Community.class);
		return searchHits.stream().map(SearchHit::getContent).toList();
	}

	@Override
	public List<Community> findByKeywordAndPostType(String keyword, String postType) {
		String queryString = String.format("""
			{
				"query": {
					"bool": {
						"must": [
							{
								"multi_match": {
									"query": "%s",
									"fields": ["title^2", "content"]
								}
							}
						],
						"filter": [
							{
								"term": {
									"postType": "%s"
								}
							},
							{
								"term": {
									"isBlocked": "N"
								}
							}
						]
					}
				},
				"sort": [
					{
						"createdAt": "desc"
					}
				]
			}
			""", keyword, postType);

		Query query = new StringQuery(queryString);
		SearchHits<Community> searchHits = operations.search(query, Community.class);
		return searchHits.stream().map(SearchHit::getContent).toList();
	}

	@Override
	public List<Community> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, int from, int size) {
		String queryString = String.format("""
			{
				"query": {
					"range": {
						"createdAt": {
							"gte": "%s",
							"lte": "%s"
						}
					}
				},
				"sort": [
					{
						"createdAt": "desc"
					}
				],
				"from": %d,
				"size": %d
			}
			""", startDate, endDate, from, size);

		Query query = new StringQuery(queryString);
		SearchHits<Community> searchHits = operations.search(query, Community.class);
		return searchHits.stream().map(SearchHit::getContent).toList();
	}

	@Override
	public List<Community> findByKeywordOrAuthor(String keyword, Long authorId) {
		String queryString = String.format("""
			{
				"query": {
					"bool": {
						"should": [
							{
								"multi_match": {
									"query": "%s",
									"fields": ["title^3", "content^2"]
								}
							},
							{
								"match": {
									"authorId": %d
								}
							}
						],
						"minimum_should_match": 1
					}
				}
			}
			""", keyword, authorId);

		Query query = new StringQuery(queryString);
		SearchHits<Community> searchHits = operations.search(query, Community.class);
		return searchHits.stream().map(SearchHit::getContent).toList();
	}

	@Override
	public List<CommunityDocument> findByKeywordFuzzy(String keyword) {
		String queryString = String.format("""
			{
				"multi_match": {
					"query": "%s",
					"fields": ["title^2", "content"],
					"fuzziness": 2,
					"prefix_length": 0,
					"minimum_should_match": "50%%"
				}
			}
			""", keyword);

		Query query = new StringQuery(queryString);
		SearchHits<CommunityDocument> searchHits = operations.search(query, CommunityDocument.class);
		return searchHits.stream().map(SearchHit::getContent).toList();
	}

	@Override
	public SearchHits<Community> findByKeywordWithHighlight(String keyword) {
		String queryString = String.format("""
			{
				"query": {
					"multi_match": {
						"query": "%s",
						"fields": ["title", "content"]
					}
				},
				"highlight": {
					"fields": {
						"title": {
							"fragment_size": 150,
							"number_of_fragments": 3
						}
					}
				}
			}
			""", keyword);

		Query query = new StringQuery(queryString);
		return operations.search(query, Community.class);
	}

	@Override
	public SearchHits<Community> getPostStatistics() {
		String queryString = """
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
			""";

		Query query = new StringQuery(queryString);
		return operations.search(query, Community.class);
	}
} 
package com.example.screen_golf.community.repository.elasticsearch;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.screen_golf.community.domain.Community;

public interface CommunityElasticSearchRepository extends ElasticsearchRepository<Community, Long> {

	List<Community> findByKeyword(String keyword);
}

package com.example.screen_golf.community.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.screen_golf.community.domain.Community;

public interface CommunitySearchRepository extends ElasticsearchRepository<Community, Long> {
}

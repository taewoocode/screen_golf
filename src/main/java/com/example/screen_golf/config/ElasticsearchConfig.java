package com.example.screen_golf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.screen_golf.community.repository.elasticsearch")
public class ElasticsearchConfig {
}

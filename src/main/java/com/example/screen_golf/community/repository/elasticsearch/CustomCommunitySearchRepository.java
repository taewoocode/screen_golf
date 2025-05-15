package com.example.screen_golf.community.repository.elasticsearch;

import java.time.LocalDateTime;
import java.util.List;

import com.example.screen_golf.community.domain.Community;
import com.example.screen_golf.community.domain.CommunityDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface CustomCommunitySearchRepository {
    List<Community> findByKeyword(String keyword);
    List<Community> findByKeywordAndPostType(String keyword, String postType);
    List<Community> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, int from, int size);
    List<Community> findByKeywordOrAuthor(String keyword, Long authorId);
    List<CommunityDocument> findByKeywordFuzzy(String keyword);
    SearchHits<Community> findByKeywordWithHighlight(String keyword);
    SearchHits<Community> getPostStatistics();
} 
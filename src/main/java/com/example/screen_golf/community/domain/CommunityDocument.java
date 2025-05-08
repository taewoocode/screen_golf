package com.example.screen_golf.community.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(indexName = "community")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Text, analyzer = "standard")
	private String title;

	@Field(type = FieldType.Text, analyzer = "standard")
	private String content;

	@Field(type = FieldType.Keyword)
	private String postType;

	@Field(type = FieldType.Keyword)
	private String hasAttachment;

	@Field(type = FieldType.Keyword)
	private String isBlocked;

	@Field(type = FieldType.Long)
	private Long authorId;

	@Field(type = FieldType.Date)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Date)
	private LocalDateTime updatedAt;
}

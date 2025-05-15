package com.example.screen_golf.community.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.DateFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "community_v2")
@Setting(settingPath = "elasticsearch-settings.json")
public class CommunityDocument {

	@Id
	@Field(type = FieldType.Keyword)
	private String id;

	@Field(type = FieldType.Long)
	private Long authorId;

	@Field(type = FieldType.Keyword)
	private String authorName;

	@Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "nori_analyzer")
	private String title;

	@Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "nori_analyzer")
	private String content;

	@Field(type = FieldType.Keyword)
	private String postType;

	@Field(type = FieldType.Integer)
	private Integer postNumber;

	@Field(type = FieldType.Integer)
	private Integer replyNumber;

	@Field(type = FieldType.Integer)
	private Integer parentReplyNumber;

	@Field(type = FieldType.Keyword)
	private String isBlocked;

	@Field(type = FieldType.Keyword)
	private String hasAttachment;

	@Field(type = FieldType.Date, format = DateFormat.date)
	private LocalDate createdAt;

	@Field(type = FieldType.Date, format = DateFormat.date)
	private LocalDate updatedAt;
}

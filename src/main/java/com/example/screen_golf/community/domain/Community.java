package com.example.screen_golf.community.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Integer postNumber;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private PostType postType;

	@Column(nullable = false)
	private Integer replyNumber;

	@Column
	private Integer parentReplyNumber;

	@Column(length = 100)
	private String taggedUserId;

	@Column(columnDefinition = "TEXT")
	private String replyContent;

	@Column(nullable = false, length = 1)
	private String hasAttachment = "N";

	@Column(nullable = false, length = 1)
	private String isBlocked;

	@Column(length = 50)
	private String blockType;

	@Column(nullable = false)
	private Long authorId;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public Community(Integer postNumber, String title, String content, PostType postType, Integer replyNumber,
		Integer parentReplyNumber, String taggedUserId, String replyContent, String hasAttachment,
		String isBlocked, String blockType, Long authorId) {
		this.postNumber = postNumber;
		this.title = title;
		this.content = content;
		this.postType = postType;
		this.replyNumber = replyNumber;
		this.parentReplyNumber = parentReplyNumber;
		this.taggedUserId = taggedUserId;
		this.replyContent = replyContent;
		this.hasAttachment = hasAttachment != null ? hasAttachment : "N";
		this.isBlocked = isBlocked;
		this.blockType = blockType;
		this.authorId = authorId;
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
		this.updatedAt = LocalDateTime.now();
	}
}

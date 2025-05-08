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
import jakarta.persistence.SequenceGenerator;
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

	/** 게시글 번호 */
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_post_no_seq")
	@SequenceGenerator(name = "community_post_no_seq", sequenceName = "community_post_no_seq", allocationSize = 1)
	@Column(nullable = false, unique = true)
	private Integer postNumber;

	/** 게시글 제목 */
	@Column(nullable = false, length = 255)
	private String title;

	/** 게시글 내용 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	/** 게시글 구분코드 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private PostType postType;

	/** 댓글 번호 */
	@Column(nullable = false)
	private Integer replyNumber;

	/** 상위 댓글 번호 */
	@Column(nullable = true)
	private Integer parentReplyNumber;

	/** 태그된 사용자 아이디 */
	@Column(nullable = true, length = 100)
	private String taggedUserId;

	/** 댓글 내용 */
	@Column(nullable = true, columnDefinition = "TEXT")
	private String replyContent;

	/** 첨부파일 여부 */
	@Column(nullable = false, length = 1)
	private String hasAttachment = "N";

	/** 차단 여부 */
	@Column(nullable = false, length = 1)
	private String isBlocked;

	/** 차단 구분코드 */
	@Column(nullable = true, length = 50)
	private String blockType;

	/** 작성자 아이디 */
	@Column(nullable = false)
	private Long authorId;

	/** 생성 시간 */
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/** 수정 시간 */
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

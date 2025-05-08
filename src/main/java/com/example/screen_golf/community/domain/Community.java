package com.example.screen_golf.community.domain;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "community")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 게시글 번호 */
	@Column(nullable = false)
	private Integer postNo;

	/** 게시글 제목 */
	@Column(nullable = false, length = 255)
	private String postTil;

	/** 게시글 내용 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String postCnts;

	/** 게시글 구분코드 */
	@Column(nullable = false, length = 50)
	private String postDvCd;

	/** 댓글 번호 */
	@Column(nullable = false)
	private Integer rplNo;

	/** 상위 댓글 번호 */
	@Column(nullable = true)
	private Integer rplUpprNo;

	/** 태그 아이디 */
	@Column(nullable = true, length = 100)
	private String tagUserId;

	/** 댓글 내용 */
	@Column(nullable = true, columnDefinition = "TEXT")
	private String rplCnts;

	/** 첨부파일 여부 */
	@Column(nullable = false, length = 1)
	private String atcfYn = "N";

	/** 차단 여부 */
	@Column(nullable = false, length = 1)
	private String stopYn;

	/** 차단 구분코드 */
	@Column(nullable = true, length = 50)
	private String stopDvCd;

	/** 사용자 아이디 */
	@Column(nullable = false)
	private Long userId;
}

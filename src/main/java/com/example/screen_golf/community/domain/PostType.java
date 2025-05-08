package com.example.screen_golf.community.domain;

public enum PostType {
	NOTICE("공지사항"),
	QUESTION("질문"),
	GENERAL("일반"),
	ADVERT("광고");

	private final String description;

	PostType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	// 코드로부터 Enum 객체를 가져오는 메서드
	public static PostType fromCode(String code) {
		for (PostType type : values()) {
			if (type.name().equalsIgnoreCase(code)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid board type code: " + code);
	}
} 
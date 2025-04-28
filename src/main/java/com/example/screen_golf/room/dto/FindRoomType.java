package com.example.screen_golf.room.dto;

import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FindRoomType {

	/**
	 * Room 타입으로 조회 요청 DTO
	 * 클라이언트가 특정 Room 타입을 기반으로 조회할 때 사용
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomTypeRequest {
		private RoomType roomType;
	}
}

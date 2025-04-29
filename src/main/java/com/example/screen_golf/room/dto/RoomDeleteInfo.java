package com.example.screen_golf.room.dto;

import com.example.screen_golf.room.domain.Room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoomDeleteInfo {

	/**
	 * Room 삭제 요청 DTO
	 * 클라이언트가 룸 삭제 시 ID를 전달하는 방식
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomDeleteRequest {
		private Long id;
	}

	/**
	 * Room 삭제 응답 DTO
	 * 삭제 결과를 반환합니다.
	 */
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoomDeleteResponse {
		private boolean success;
		private String message;

		/**
		 * Room 엔티티를 기반으로 성공 메시지를 포함하는 RoomDeleteResponse DTO 생성
		 */
		public static RoomDeleteResponse fromEntity(Room room) {
			return RoomDeleteResponse.builder()
				.success(true)
				.message("Room (ID: " + room.getId())
				.build();
		}
	}
}

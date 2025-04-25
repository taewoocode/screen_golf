package com.example.screen_golf.room.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.Room.RoomResponse;
import com.example.screen_golf.room.domain.Room.RoomTypeRequest;
import com.example.screen_golf.room.service.RoomService;
import com.example.screen_golf.swagger.SwaggerDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Room", description = "Room 관련 API")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

	private final RoomService roomService;

	/**
	 * 전체 Room 목록 조회
	 * 이용 가능, 이용 중 등 모든 Room의 정보를 반환합니다.
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_LIST,
		description = SwaggerDocs.DESCRIPTION_ROOM_LIST
	)
	@GetMapping("/all")
	public ResponseEntity<List<RoomResponse>> getAllRooms() {
		List<RoomResponse> response = roomService.findAllRooms();
		log.info("전체 Room 정보 조회 성공 - 총 Room 갯수: {}", response.size());
		return ResponseEntity.ok(response);
	}

	/**
	 * RoomType으로 Room 조회
	 * 클라이언트가 RoomType만 전달하여 해당 타입의 Room 목록을 조회할 수 있습니다.
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_BY_TYPE,
		description = SwaggerDocs.DESCRIPTION_ROOM_BY_TYPE
	)
	@PostMapping("/search/type")
	public ResponseEntity<List<RoomResponse>> getRoomsByType(
		@Parameter(description = "검색할 Room 타입 정보", required = true)
		@RequestBody RoomTypeRequest request) {
		List<RoomResponse> response = roomService.findRoomList(request);
		log.info("Room 타입으로 조회 성공 - 타입: {}, 결과 갯수: {}",
			request.getRoomType(), response.size());
		return ResponseEntity.ok(response);
	}

	/**
	 * Room 단건 상세 조회
	 * URL PathVariable로 전달된 Room id를 기반으로 상세 정보를 반환합니다.
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_DETAIL,
		description = SwaggerDocs.DESCRIPTION_ROOM_DETAIL
	)
	@GetMapping("/{id}")
	public ResponseEntity<RoomResponse> getRoomDetail(
		@Parameter(description = "조회할 Room의 ID", required = true)
		@PathVariable Long id) {
		RoomResponse response = roomService.findById(id);
		log.info("Room 상세 조회 성공 - Room ID: {}", id);
		return ResponseEntity.ok(response);
	}

	/**
	 * Room 생성
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_CREATE,
		description = SwaggerDocs.DESCRIPTION_CREATE_ROOM
	)
	public ResponseEntity<RoomResponse> createRoomRequest(
		@Parameter(description = "룸 생성 요청", required = true)
		@RequestBody Room.RoomCreateRequest request) {
		RoomResponse room = roomService.createRoom(request);
		log.info("Room 생성 성공={}", room);
		return ResponseEntity.ok(room);
	}
}

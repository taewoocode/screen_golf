package com.example.screen_golf.room.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.dto.AvailableRoomInfo;
import com.example.screen_golf.room.dto.FindRoomType;
import com.example.screen_golf.room.dto.RoomCreateInfo;
import com.example.screen_golf.room.dto.RoomDeleteInfo;
import com.example.screen_golf.room.dto.RoomFindInfo;
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
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_LIST,
		description = SwaggerDocs.DESCRIPTION_ROOM_LIST
	)
	@GetMapping("/all")
	public ResponseEntity<?> getAllRooms() {
		try {
			List<RoomFindInfo.RoomFindResponse> allRooms = roomService.findAllRooms();
			log.info("전체 Room 목록 조회 성공 - 총 Room 갯수: {}", allRooms.size());
			return ResponseEntity.ok(allRooms);
		} catch (Exception e) {
			log.error("전체 Room 목록 조회 실패: {}", e.getMessage());
			return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
		}
	}

	/**
	 * RoomType으로 Room 조회
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_BY_TYPE,
		description = SwaggerDocs.DESCRIPTION_ROOM_BY_TYPE
	)
	@PostMapping("/search/type")
	public ResponseEntity<?> getRoomsByType(
		@Parameter(description = "검색할 Room 타입 정보", required = true)
		@RequestBody FindRoomType.RoomTypeRequest request) {
		try {
			List<FindRoomType.RoomTypeResponse> response = roomService.findRoomList(request);
			log.info("Room 타입으로 조회 성공 - 타입: {}, 결과 갯수: {}", request.getRoomType(), response.size());
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			log.error("Room 타입 조회 실패 - 잘못된 요청: {}", e.getMessage());
			return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
		} catch (Exception e) {
			log.error("Room 타입 조회 실패 - 서버 오류: {}", e.getMessage());
			return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
		}
	}

	/**
	 * Room 단건 상세 조회
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_DETAIL,
		description = SwaggerDocs.DESCRIPTION_ROOM_DETAIL
	)
	@GetMapping("/{id}")
	public ResponseEntity<?> getRoomDetail(
		@Parameter(description = "조회할 Room의 ID", required = true)
		@PathVariable Long id) {
		try {
			Room roomId = roomService.findById(id);
			log.info("Room 상세 조회 성공 - Room ID: {}", id);
			return ResponseEntity.ok(roomId);
		} catch (IllegalArgumentException e) {
			log.error("Room 상세 조회 실패 - 잘못된 요청: {}", e.getMessage());
			return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
		} catch (Exception e) {
			log.error("Room 상세 조회 실패 - 서버 오류: {}", e.getMessage());
			return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
		}
	}

	/**
	 * Room 생성
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_CREATE,
		description = SwaggerDocs.DESCRIPTION_CREATE_ROOM
	)
	@PostMapping("/create")
	public ResponseEntity<?> createRoomRequest(
		@Parameter(description = "룸 생성 요청", required = true)
		@RequestBody RoomCreateInfo.RoomCreateRequest request) {
		try {
			RoomCreateInfo.RoomCreateResponse room = roomService.createRoom(request);
			log.info("Room 생성 성공 - ID: {}, 이름: {}", room.getId(), room.getName());
			return ResponseEntity.ok(room);
		} catch (IllegalArgumentException e) {
			log.error("Room 생성 실패 - 잘못된 요청: {}", e.getMessage());
			return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
		} catch (Exception e) {
			log.error("Room 생성 실패 - 서버 오류: {}", e.getMessage());
			return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
		}
	}

	/**
	 * Room 삭제
	 */
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_DELETE,
		description = SwaggerDocs.DESCRIPTION_ROOM_DELETE
	)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRoom(
		@Parameter(description = "삭제할 Room ID", required = true)
		@PathVariable RoomDeleteInfo.RoomDeleteRequest request) {
		try {
			RoomDeleteInfo.RoomDeleteResponse roomDeleteResponse = roomService.deleteRoom(request);
			log.info("Room 삭제 성공 - ID: {}", roomDeleteResponse);
			return ResponseEntity.ok(roomDeleteResponse);
		} catch (IllegalArgumentException e) {
			log.error("Room 삭제 실패 - 잘못된 요청: {}", e.getMessage());
			return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
		} catch (Exception e) {
			log.error("Room 삭제 실패 - 서버 오류: {}", e.getMessage());
			return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
		}
	}

	@PostMapping("available")
	@Operation(
		summary = SwaggerDocs.SUMMARY_ROOM_AVAILABLE_LIST,
		description = SwaggerDocs.DESCRIPTION_ROOM_AVAILABLE_LIST
	)
	public ResponseEntity<List<AvailableRoomInfo.AvailableRoomResponse>> findAvailableRooms(
		@Parameter(description = "이용가능한 Room", required = true)
		@RequestBody AvailableRoomInfo.AvailableRoomRequest availableRoomRequest
	) {
		List<AvailableRoomInfo.AvailableRoomResponse> availableRoomResponses
			= roomService.availableRoom(availableRoomRequest);
		log.info("이용가능한 Room조회 성공={}", availableRoomRequest);
		return ResponseEntity.ok(availableRoomResponses);
	}

}
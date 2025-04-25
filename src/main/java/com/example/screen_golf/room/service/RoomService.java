package com.example.screen_golf.room.service;

import java.util.List;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;

public interface RoomService {

	/**
	 * 특정 Room 타입으로 필터링하여 조회
	 *
	 * @param request Room 타입(예: VIP, STANDARD 등)을 담은 DTO
	 * @return 해당 타입에 속하는 RoomResponse 목록
	 */
	List<Room.RoomResponse> findRoomList(Room.RoomTypeRequest request);

	/**
	 * 모든 Room 목록을 조회(상태 관계 없이 전체 데이터를 반환)
	 *
	 * @return 모든 RoomResponse 목록
	 */
	List<Room.RoomResponse> findAllRooms();

	/**
	 * 특정 ID에 해당하는 Room의 상세 정보를 조회
	 *
	 * @param id 조회할 Room의 식별자
	 * @return RoomResponse DTO
	 */
	Room.RoomResponse findById(Long id);

	/**
	 * 새로운 Room을 생성합니다.
	 *
	 * @param createRequest Room 생성
	 * @return 생성된 Room의 정보를 담은 RoomResponse DTO
	 */
	Room.RoomResponse createRoomTypeVIP(Room.RoomCreateRequest createRequest);

	/**
	 * 기존 Room의 정보를 수정합니다.
	 *
	 * @param updateRequest 수정
	 * @return 수정 후의 Room 정보를 담은 RoomResponse DTO
	 */
	Room.RoomResponse updateRoom(Room.RoomUpdateRequest updateRequest);

	/**
	 * Room의 상태를 변경합니다.
	 *
	 * @param id 상태를 변경할 Room의 식별자
	 * @param newStatus 변경할 새로운 상태 (예: AVAILABLE, RESERVED, IN_USE, MAINTENANCE)
	 * @return 상태 변경 후의 Room 정보를 담은 RoomResponse DTO
	 */
	Room.RoomResponse changeRoomStatus(Long id, RoomStatus newStatus);

	/**
	 * 특정 Room을 삭제합니다.
	 *
	 * @param id 삭제할 Room의 식별자
	 * @return 삭제 완료 후의 결과 메시지를 담은 RoomDeleteResponse DTO
	 */
	Room.RoomDeleteResponse deleteRoom(Long id);
}

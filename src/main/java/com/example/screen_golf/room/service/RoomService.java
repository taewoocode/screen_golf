package com.example.screen_golf.room.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.dto.AvailableRoomInfo;
import com.example.screen_golf.room.dto.FindRoomType;
import com.example.screen_golf.room.dto.RoomCreateInfo;
import com.example.screen_golf.room.dto.RoomDeleteInfo;
import com.example.screen_golf.room.dto.RoomFindInfo;
import com.example.screen_golf.room.dto.RoomUpdateInfo;

public interface RoomService {

	/**
	 * 특정 Room 타입으로 필터링하여 조회
	 *
	 * @param request Room 타입(예: VIP, STANDARD 등)을 담은 DTO
	 * @return 해당 타입에 속하는 RoomResponse 목록
	 */
	List<FindRoomType.RoomTypeResponse> findRoomList(FindRoomType.RoomTypeRequest request);

	/**
	 * 모든 Room 목록을 조회(상태 관계 없이 전체 데이터를 반환)
	 *
	 * @return 모든 RoomResponse 목록
	 */
	List<RoomFindInfo.RoomFindResponse> findAllRooms();

	/**
	 * 특정 ID에 해당하는 Room의 상세 정보를 조회
	 *
	 * @param id 조회할 Room의 식별자
	 * @return RoomResponse DTO
	 */
	Room findById(Long id);

	/**
	 * 새로운 Room을 생성합니다.
	 *
	 * @param createRequest Room 생성
	 * @return 생성된 Room의 정보를 담은 RoomResponse DTO
	 */ // Room 생성 메소드
	RoomCreateInfo.RoomCreateResponse createRoom(RoomCreateInfo.RoomCreateRequest createRequest);

	/**
	 * 기존 Room의 정보를 수정합니다.
	 *
	 * @param updateRequest 수정
	 * @return 수정 후의 Room 정보를 담은 RoomResponse DTO
	 */
	// Room 수정 메소드 - 인터페이스에 선언된 시그니처와 일치해야 합니다.
	@Transactional
	RoomUpdateInfo.RoomUpdateResponse updateRoom(RoomUpdateInfo.RoomUpdateRequest updateRequest);

	/**
	 * 특정 Room을 삭제합니다.
	 *
	 * @param id 삭제할 Room의 식별자
	 * @return 삭제 완료 후의 결과 메시지를 담은 RoomDeleteResponse DTO
	 */
	RoomDeleteInfo.RoomDeleteResponse deleteRoom(RoomDeleteInfo.RoomDeleteRequest request);

	/**
	 * 이용 가능한 Room 조회
	 */
	List<AvailableRoomInfo.AvailableRoomResponse> availableRoom(
		AvailableRoomInfo.AvailableRoomRequest availableRoomRequest);
}

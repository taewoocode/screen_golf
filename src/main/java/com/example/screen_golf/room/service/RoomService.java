package com.example.screen_golf.room.service;

import java.util.List;

import com.example.screen_golf.room.domain.Room;

public interface RoomService {

	List<Room.RoomResponse> findRoomList(Room.RoomTypeRequest request);

	/**
	 * 전체 Room 목록 조회 (상태 관계 없이 모든 Room)
	 * @return
	 */
	List<Room.RoomResponse> findAllRooms();

	Room.RoomResponse findById(Long id);

}

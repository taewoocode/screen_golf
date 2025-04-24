package com.example.screen_golf.room.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;
import com.example.screen_golf.room.respository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	/**
	 * Type으로 필터링
	 * @param request
	 * @return
	 */
	@Override
	public List<Room.RoomResponse> findRoomList(Room.RoomTypeRequest request) {
		RoomType roomType = request.getRoomType();
		List<Room> roomList = roomRepository.findByRoomType(roomType);
		return roomList.stream()
			.map(Room.RoomResponse::fromEntity)
			.collect(Collectors.toList());
	}

	/**
	 * 모든 Room 조회 (이용중, 이용중이지 않음)
	 * @return
	 */
	@Override
	public List<Room.RoomResponse> findAllRooms() {
		List<Room> allRoom = roomRepository.findAll();
		return allRoom.stream()
			.map(Room.RoomResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	public Room.RoomResponse findById(Long id) {
		Room room = roomRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 룸을 찾을 수 없습니다."));
		return Room.RoomResponse.fromEntity(room);
	}

}

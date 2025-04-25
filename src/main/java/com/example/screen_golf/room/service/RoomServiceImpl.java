package com.example.screen_golf.room.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.screen_golf.exception.room.RoomNotFoundException;
import com.example.screen_golf.exception.room.RoomStateException;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomStatus;
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
		Room room = validationRoom(id);
		return Room.RoomResponse.fromEntity(room);
	}

	@Override
	public Room.RoomResponse createRoomTypeVIP(Room.RoomCreateRequest createRequest) {

		if (roomRepository.existsByName(createRequest.getName())) {
			throw new RoomStateException("이미 존재하는 룸 이름입니다.");
		}
		if (roomRepository.findByName(createRequest.getName())
			.map(room -> room.getStatus() == RoomStatus.IN_USE)
			.orElse(false)) {
			throw new RoomStateException("해당 룸은 현재 사용 중입니다.");
		}

		Room savedRoom = roomRepository.save(createRequest.toEntity(RoomStatus.IN_USE));
		Room savedCreateRoomTypeVIP = roomRepository.save(savedRoom);
		return Room.RoomResponse.fromEntity(savedCreateRoomTypeVIP);
	}

	@Override
	public Room.RoomResponse updateRoom(Room.RoomUpdateRequest updateRequest) {
		Room room = validationRoom(updateRequest.getId());
		updateRequest.apply(room);
		return Room.RoomResponse.fromEntity(room);
	}

	@Override
	public Room.RoomResponse changeRoomStatus(Long id, RoomStatus newStatus) {
		Room room = validationRoom(id);
		room.changeStatus(newStatus);
		return Room.RoomResponse.fromEntity(room);
	}

	@Override
	public Room.RoomDeleteResponse deleteRoom(Long id) {
		Room room = validationRoom(id);
		roomRepository.delete(room);
		return Room.RoomDeleteResponse.fromEntity(room);
	}

	private Room validationRoom(Long updateRequest) {
		return roomRepository.findById(updateRequest)
			.orElseThrow(() -> new RoomNotFoundException("해당 룸을 찾을 수 없습니다."));
	}
}

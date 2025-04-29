package com.example.screen_golf.room.service;

import static com.example.screen_golf.room.dto.RoomCreateInfo.RoomCreateRequest.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.exception.room.RoomCreateException;
import com.example.screen_golf.exception.room.RoomNotFoundException;
import com.example.screen_golf.exception.room.RoomUpdateException;
import com.example.screen_golf.room.domain.Room;
import com.example.screen_golf.room.domain.RoomType;
import com.example.screen_golf.room.dto.FindRoomType;
import com.example.screen_golf.room.dto.RoomCreateInfo;
import com.example.screen_golf.room.dto.RoomDeleteInfo;
import com.example.screen_golf.room.dto.RoomFindInfo;
import com.example.screen_golf.room.dto.RoomUpdateInfo;
import com.example.screen_golf.room.respository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	/**
	 * Room 타입에 따른 목록 조회
	 */
	@Override
	public List<FindRoomType.RoomTypeResponse> findRoomList(FindRoomType.RoomTypeRequest request) {
		try {
			RoomType roomType = request.getRoomType();
			List<Room> roomList = roomRepository.findByRoomType(roomType);
			return roomList.stream()
				.map(FindRoomType.RoomTypeResponse::fromEntity)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("findRoomList 예외 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public List<RoomFindInfo.RoomFindResponse> findAllRooms() {
		try {
			List<Room> allRooms = roomRepository.findAll();
			return allRooms.stream()
				.map(RoomFindInfo.RoomFindResponse::fromEntity)
				.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("findAllRooms 예외 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 단건 Room 조회 (ID 기준)
	 */
	@Override
	public Room findById(Long id) {
		try {
			return roomRepository.findById(id)
				.orElseThrow(() -> new RoomNotFoundException("해당 룸을 찾을 수 없습니다. ID=" + id));
		} catch (Exception e) {
			log.error("findById 예외 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Room 생성 (중복 이름 확인 후 엔티티 변환 및 저장)
	 */
	@Transactional
	@Override
	public RoomCreateInfo.RoomCreateResponse createRoom(RoomCreateInfo.RoomCreateRequest createRequest) {
		try {
			Room room = toEntity(createRequest);
			Room savedRoom = roomRepository.save(room);
			log.info("Room 생성 성공 - ID: {}, 이름: {}", savedRoom.getId(), savedRoom.getName());
			return RoomCreateInfo.RoomCreateResponse.fromEntity(savedRoom);
		} catch (Exception e) {
			log.error("Room 생성 실패 - 요청 데이터: {}", createRequest, e);
			throw new RoomCreateException("Room 생성 중 오류가 발생했습니다.");
		}
	}

	/**
	 * Room Update
	 * @param updateRequest 수정
	 * @return
	 */
	@Transactional
	@Override
	public RoomUpdateInfo.RoomUpdateResponse updateRoom(RoomUpdateInfo.RoomUpdateRequest updateRequest) {
		try {
			Room room = roomRepository.findById(updateRequest.getId())
				.orElseThrow(() -> new RoomNotFoundException("Room의 ID를 조회할 수 없습니다. " + updateRequest.getId()));
			updateRoom(updateRequest, room);
			Room updatedRoom = roomRepository.save(room);
			log.info("Room 업데이트 성공 - ID: {}, 이름: {}", updatedRoom.getId(), updatedRoom.getName());
			return RoomUpdateInfo.RoomUpdateResponse.fromEntity(updatedRoom);
		} catch (RoomNotFoundException e) {
			log.error("Room 찾기 실패 - {}", e.getMessage(), e);
			throw e;
		} catch (RoomUpdateException e) {
			log.error("Room 업데이트 실패 - {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
			throw new RoomUpdateException("Room 업데이트 중 오류가 발생했습니다.");
		}
	}

	/**
	 * Room 삭제
	 */
	@Transactional
	@Override
	public RoomDeleteInfo.RoomDeleteResponse deleteRoom(RoomDeleteInfo.RoomDeleteRequest request) {
		try {
			Room room = roomRepository.findById(request.getId())
				.orElseThrow(() -> new RoomNotFoundException("해당 룸을 찾을 수 없습니다. ID=" + request.getId()));

			roomRepository.delete(room);
			log.info("Room 삭제 성공 - ID: {}", room.getId());
			return RoomDeleteInfo.RoomDeleteResponse.fromEntity(room);
		} catch (Exception e) {
			log.error("deleteRoom 예외 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	private void updateRoom(RoomUpdateInfo.RoomUpdateRequest updateRequest, Room room) {
		room.updateRoomName(updateRequest.getName());
		room.updateReservationDate(updateRequest.getReservationDate());
		room.updateStartTime(updateRequest.getStartTime());
		room.updateEndTime(updateRequest.getStartTime().plusHours(updateRequest.getUsageDurationInHours()));
		room.updateRoomType(updateRequest.getRoomType());
		room.updateRoomPrice(updateRequest.getPricePerHour());
		room.updateUserCount(updateRequest.getUserCount());
		room.updateRoomDescription(updateRequest.getDescription());
	}
}

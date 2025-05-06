// package com.example.screen_golf.room.service;
//
// import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.List;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.example.screen_golf.room.domain.Room;
// import com.example.screen_golf.room.domain.RoomStatus;
// import com.example.screen_golf.room.domain.RoomType;
// import com.example.screen_golf.room.dto.AvailableRoomInfo;
// import com.example.screen_golf.room.repository.RoomRepository;
//
// @ExtendWith(MockitoExtension.class)
// class RoomServiceImplTest {
//
// 	@Mock
// 	private RoomRepository roomRepository;
//
// 	@InjectMocks
// 	private RoomServiceImpl roomService;
//
// 	@Test
// 	@DisplayName("이용가능한 room을 타입에 맞게 조회한다.")
// 	void getAvailableRoom() throws Exception {
// 		LocalDate reservationDay = LocalDate.of(2025, 5, 1);
// 		LocalTime startTime = LocalTime.of(10, 0);
// 		int durationTime = 5;
// 		int userCount = 3;
// 		RoomType standard = RoomType.STANDARD;
//
// 		AvailableRoomInfo.AvailableRoomRequest request = new AvailableRoomInfo.AvailableRoomRequest(
// 			reservationDay,
// 			startTime,
// 			durationTime,
// 			userCount
// 			, standard
// 		);
//
// 		Room room = Room.builder()
// 			.name("스탠다드룸")
// 			.status(RoomStatus.AVAILABLE)
// 			.roomType(RoomType.STANDARD)
// 			.pricePerHour(20000)
// 			.description("기본형 룸")
// 			.reservationDate(reservationDay)
// 			.startTime(startTime)
// 			.endTime(startTime.plusHours(durationTime))
// 			.userCount(userCount)
// 			.build();
//
// 		when(roomRepository.findAvailableRooms(reservationDay, startTime, durationTime, userCount, standard))
// 			.thenReturn(List.of(room));
//
// 		// when
// 		List<AvailableRoomInfo.AvailableRoomResponse> result = roomService.availableRoom(request);
//
// 		// then
// 		assertThat(result).hasSize(1);
// 		assertThat(result.get(0).getRoomName()).isEqualTo("스탠다드룸");
// 		assertThat(result.get(0).getRoomType()).isEqualTo(RoomType.STANDARD);
// 		assertThat(result.get(0).getRoomStatus()).isEqualTo(RoomStatus.AVAILABLE);
// 		assertThat(result.get(0).getPricePerHour()).isEqualTo(20000);
// 	}
// }
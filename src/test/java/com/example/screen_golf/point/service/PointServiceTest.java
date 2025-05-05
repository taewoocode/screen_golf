package com.example.screen_golf.point.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.screen_golf.point.dto.PointChargeInfo;
import com.example.screen_golf.point.repository.PointRepository;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

	@Mock
	private PointRepository pointRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private PointService pointService;

	@Test
	@DisplayName("정상적인 포인트 충전 동작 확인")
	void testPointChargeSuccess() {
		// Given: 테스트용 사용자와 충전 금액 셋업
		long userId = 1L;
		int chargeAmount = 5000;
		User testUser = new User("testUser", "testPassword", "taewoo", "0000-0000-0000",
			UserRole.USER, UserStatus.ACTIVE, "testImage");
		ReflectionTestUtils.setField(testUser, "id", userId);

		// 사용자 repository가 해당 사용자를 반환하도록 세팅
		when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
		// pointRepository.save()는 아무런 작업 없이 진행
		when(pointRepository.save(any())).thenAnswer(l -> l.getArgument(0));

		// When: 포인트 충전 요청
		PointChargeInfo.PointChargeResponse response = pointService.requestPointCharge(
			PointChargeInfo.PointChargeRequest.builder()
				.userId(userId)
				.amount(chargeAmount)
				.build());

		assertThat(response).isNotNull();
		assertThat(response.getUserId()).isEqualTo(userId);
		assertThat(response.getChargedAmount()).isEqualTo(chargeAmount);
		assertThat(response.getMessage()).isEqualTo("포인트 충전이 완료되었습니다.");
		verify(pointRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("3000원을 충전하면 테스트 실패")
	void testPointChargeFailed() {
		// Given: 테스트용 사용자와 충전 금액 셋업
		long userId = 1L;
		int chargeAmount = 3000;
		User testUser = new User("testUser", "testPassword", "taewoo", "0000-0000-0000",
			UserRole.USER, UserStatus.ACTIVE, "testImage");
		ReflectionTestUtils.setField(testUser, "id", userId);

		// 사용자 repository가 해당 사용자를 반환하도록 세팅
		when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
		// pointRepository.save()는 아무런 작업 없이 진행
		when(pointRepository.save(any())).thenAnswer(l -> l.getArgument(0));

		// When: 포인트 충전 요청
		PointChargeInfo.PointChargeResponse response = pointService.requestPointCharge(
			PointChargeInfo.PointChargeRequest.builder()
				.userId(userId)
				.amount(chargeAmount)
				.build());

		assertThat(response).isNotNull();
		assertThat(response.getUserId()).isEqualTo(userId);
		assertThat(response.getChargedAmount()).isEqualTo(chargeAmount);
		assertThat(response.getMessage()).isEqualTo("포인트 충전이 완료되었습니다.");
		verify(pointRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("사용자 미존재시 포인트 충전 시 예외 발생")
	void testPointChargeUserNotFound() {
		// Given: 존재하지 않는 사용자 ID와 충전 금액
		long userId = 2L;
		int chargeAmount = 5000;

		// userRepository가 Optional.empty() 반환하도록 세팅
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// When: 포인트 충전 시도를 하며, IllegalArgumentException이 발생하는지 캡처
		Throwable thrown = catchThrowable(() ->
			pointService.requestPointCharge(
				PointChargeInfo.PointChargeRequest.builder()
					.userId(userId)
					.amount(chargeAmount)
					.build()
			)
		);

		// Then: 예상한 예외가 발생했는지 검증
		assertThat(thrown)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("사용자를 찾을 수 없습니다.");
	}
}

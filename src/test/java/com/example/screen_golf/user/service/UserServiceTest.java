package com.example.screen_golf.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.screen_golf.jwts.JwtProvider;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.dto.UserLoginInfo;
import com.example.screen_golf.user.dto.UserSignUpInfo;
import com.example.screen_golf.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	private UserSignUpInfo.UserSignUpRequest request;

	@BeforeEach
	void setUp() {
		request = UserSignUpInfo.UserSignUpRequest.builder()
			.email("test@example.com")
			.password("password123")
			.name("Test User")
			.phone("010-1234-5678")
			.profileImage("profile.jpg")
			.build();
	}

	@Test
	@DisplayName("회원가입진행")
	void 회원가입을_진행() {
		// given
		User user = User.builder()
			.email(request.getEmail())
			.password("encodedPassword")
			.name(request.getName())
			.phone(request.getPhone())
			.profileImage(request.getProfileImage())
			.role(UserRole.USER)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false); // 이메일 중복 없음
		when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		// when
		UserSignUpInfo.UserSignUpResponse response = userService.registerUser(request);

		// then
		assertNotNull(response);
		assertEquals("test@example.com", response.getEmail());
		assertEquals("Test User", response.getName());
		assertEquals(UserRole.USER, response.getRole());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void registerUser_EmailExists_Failure() {
		// given
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

		// when / then
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			userService.registerUser(request);
		});

		assertEquals("이미 존재하는 이메일입니다.", thrown.getMessage());
	}

	@Test
	void 페이로드_값_확인() throws Exception {
		UserSignUpInfo.UserSignUpRequest userSignUpRequest = new UserSignUpInfo.UserSignUpRequest();
		String jsonPayload = new ObjectMapper().writeValueAsString(userSignUpRequest);
		log.info("payload={}", jsonPayload);

		UserSignUpInfo.UserSignUpRequest request2 = new UserSignUpInfo.UserSignUpRequest(
			"TestUser", "testPassWord", "testUser", "testPhone", "testImage"
		);
		String jsonPayload2 = new ObjectMapper().writeValueAsString(request2);
		log.info("payload2={}", jsonPayload2);
	}

	@Test
	@DisplayName("로그인을 진행한다.")
	void login() throws Exception {

		User user = new User("testEmail", "testPassword", "testName",
			"testPhone", UserRole.USER, UserStatus.ACTIVE, "testImage");

		UserLoginInfo.UserLoginRequest pa = new UserLoginInfo.UserLoginRequest("test@example.com", "password");

		// UserLoginInfo.UserLoginResponse generatedToken = new UserLoginInfo.UserLoginResponse(1L, "test@example.com",
		// 	"generated_token");

	}

	// @Test
	// @DisplayName("실제 JWT 토큰을 생성하고 로그인한다.")
	// void login_with_real_jwt() throws Exception {
	// 	User user = User.builder()
	// 		.email("test@example.com")
	// 		.password(new BCryptPasswordEncoder().encode("password"))
	// 		.name("testName")
	// 		.phone("testPhone")
	// 		.role(UserRole.USER)
	// 		.status(UserStatus.ACTIVE)
	// 		.profileImage("testImage")
	// 		.build();
	//
	// 	ReflectionTestUtils.setField(user, "id", 1L);
	//
	// 	UserLoginInfo.UserLoginRequest loginRequest =
	// 		new UserLoginInfo.UserLoginRequest("test@example.com", "password");
	//
	// 	JwtProvider jwtToken = new JwtProvider("your-secret-key", 3600000L);
	// 	UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder, jwtToken);
	//
	// 	given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
	// 	given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
	//
	// 	UserLoginInfo.UserLoginResponse response = userService.login(loginRequest);
	// 	log.info("JWT={}", response.getToken());
	// 	log.info("userId={}", response.getUserId());
	// 	log.info("userEmail={}", response.getEmail());
	//
	// 	Assertions.assertThat(response.getUserId()).isEqualTo(1L);
	// 	Assertions.assertThat(response.getEmail()).isEqualTo("test@example.com");
	// 	Assertions.assertThat(response.getToken()).isNotBlank();
	//
	// }

	@Test
	@DisplayName("존재하지 않는 이메일로 로그인 시도")
	void loginUserNotFound() {
		// given
		UserLoginInfo.UserLoginRequest loginRequest = new UserLoginInfo.UserLoginRequest("wrong@example.com",
			"password");

		// Mocking user repository to return empty for non-existing email
		given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());  // 유저가 존재하지 않으면

		// when / then
		assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequest));  // 예외 발생
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않는 경우")
	void loginWrongPassword() {
		// given
		User user = User.builder()
			.email("test@example.com")
			.password("encodedPassword")
			.name("testName")
			.phone("testPhone")
			.role(UserRole.USER)
			.status(UserStatus.ACTIVE)
			.profileImage("testImage")
			.build();

		UserLoginInfo.UserLoginRequest loginRequest = new UserLoginInfo.UserLoginRequest("test@example.com",
			"wrongPassword");

		// Mocking user repository and password encoder
		given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user)); // 유저 조회
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false); // 비밀번호 불일치

		// when / then
		assertThrows(IllegalArgumentException.class, () -> userService.login(loginRequest));  // 예외 발생
	}
}

package com.example.screen_golf.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.jwts.JwtProvider;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.dto.UserConverter;
import com.example.screen_golf.user.dto.UserLoginInfo;
import com.example.screen_golf.user.dto.UserLookUpId;
import com.example.screen_golf.user.dto.UserLookUpName;
import com.example.screen_golf.user.dto.UserSignUpInfo;
import com.example.screen_golf.user.repository.UserRepository;
import com.example.screen_golf.utils.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final RedisUtil redisUtil;
	private final UserConverter userConverter;

	private static final String LOGIN_STATUS_PREFIX = "login:status:";
	private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";
	private static final long LOGIN_STATUS_EXPIRATION = 24 * 60 * 60 * 1000; // 24시간

	private static final String STATUS_ACTIVE = "ACTIVE";
	private static final String STATUS_INACTIVE = "INACTIVE";

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	@Override
	@Transactional
	public UserSignUpInfo.UserSignUpResponse registerUser(UserSignUpInfo.UserSignUpRequest request) {
		try {
			log.info("회원가입 요청 시작 - 이메일: {}", request.getEmail());
			if (userRepository.existsByEmail(request.getEmail())) {
				log.warn("이미 존재하는 이메일: {}", request.getEmail());
				throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
			}
			User user = userConverter.makeEntity(request);
			log.info("생성된 User 객체 - ID: {}, 이메일: {}", user.getId(), user.getEmail());

			User savedUser = userRepository.save(user);
			String generateToken = jwtProvider.generateToken(savedUser.getId());
			log.info("저장된 User 객체 - ID: {}, 이메일: {}", savedUser.getId(), savedUser.getEmail());
			UserSignUpInfo.UserSignUpResponse signUpResponse = userConverter.makeSignUpResponse(savedUser,
				generateToken);
			log.info("회원가입 완료 - 사용자 ID: {}", signUpResponse.getUserId());
			return signUpResponse;
		} catch (IllegalArgumentException e) {
			log.error("회원가입 실패 (유효성 검사) - 이메일: {}", request.getEmail(), e);
			throw e;
		} catch (Exception e) {
			log.error("회원가입 실패 (서버 오류) - 이메일: {}", request.getEmail(), e);
			throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserLookUpId.UserLookUpIdResponse findUser(UserLookUpId.UserLookUpIdRequest request) {
		try {
			log.info("회원 정보 조회 시작 - 사용자 ID: {}", request.getUserId());
			User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
			log.info("회원 정보 조회 완료 - 사용자 ID: {}", userConverter.makeFindUserEntity(user).getUserId());
			return userConverter.makeFindUserEntity(user);
		} catch (IllegalArgumentException e) {
			log.error("회원 정보 조회 실패 (유효성 검사) - 사용자 ID: {}", request.getUserId(), e);
			throw e;
		} catch (Exception e) {
			log.error("회원 정보 조회 실패 (서버 오류) - 사용자 ID: {}", request.getUserId(), e);
			throw new RuntimeException("회원 정보 조회 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserLookUpName.UserLookUpNameResponse findUser(UserLookUpName.UserLookUpNameRequest request) {
		try {
			log.info("이름으로 회원 정보 조회 시작 - 이름: {}", request.getName());
			User user = userRepository.findByName(request.getName())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이름입니다."));
			UserLookUpName.UserLookUpNameResponse findNameUserResponse = userConverter.makeUserLookUpNameEntity(user);
			log.info("이름으로 회원 정보 조회 완료 - 사용자 ID: {}", findNameUserResponse.getUserId());
			return findNameUserResponse;
		} catch (IllegalArgumentException e) {
			log.error("이름으로 회원 정보 조회 실패 (유효성 검사) - 이름: {}", request.getName(), e);
			throw e;
		} catch (Exception e) {
			log.error("이름으로 회원 정보 조회 실패 (서버 오류) - 이름: {}", request.getName(), e);
			throw new RuntimeException("이름으로 회원 정보 조회 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	@Transactional
	public UserLoginInfo.UserLoginResponse login(UserLoginInfo.UserLoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 다릅니다.");
		}

		String loginKey = LOGIN_STATUS_PREFIX + user.getId();
		String currentStatus = redisUtil.getData(loginKey);

		if (STATUS_INACTIVE.equals(currentStatus)) {
			log.info("User {} is logging in after logout", user.getId());
		}

		redisUtil.setDataExpire(loginKey, STATUS_ACTIVE, LOGIN_STATUS_EXPIRATION);

		String accessToken = jwtProvider.generateToken(user.getId());
		String refreshToken = jwtProvider.generateRefreshToken(user.getId());

		String refreshKey = REFRESH_TOKEN_PREFIX + user.getId();
		redisUtil.setDataExpire(refreshKey, refreshToken, jwtProvider.getRefreshTokenExpiration());

		log.info("User {} logged in successfully", user.getId());

		return UserLoginInfo.UserLoginResponse.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * 유저상태 추적
	 * @param userId
	 * @return
	 */
	@Override
	public boolean isUserLoggedIn(Long userId) {
		String loginKey = LOGIN_STATUS_PREFIX + userId;
		String status = redisUtil.getData(loginKey);
		return STATUS_ACTIVE.equals(status);
	}

	/**
	 * Logout
	 * @param userId
	 */
	@Override
	public void logout(Long userId) {
		String loginKey = LOGIN_STATUS_PREFIX + userId;
		String refreshKey = REFRESH_TOKEN_PREFIX + userId;
		redisUtil.setDataExpire(loginKey, STATUS_INACTIVE, LOGIN_STATUS_EXPIRATION);
		redisUtil.setDataExpire(refreshKey, STATUS_INACTIVE, jwtProvider.getRefreshTokenExpiration());

		log.info("User {} logged out", userId);
	}

	// 리프레시 토큰으로 새로운 액세스 토큰 발급
	public String refreshAccessToken(String refreshToken) {
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
		}

		Long userId = jwtProvider.getUserIdFromToken(refreshToken);
		String refreshKey = REFRESH_TOKEN_PREFIX + userId;
		String storedRefreshToken = redisUtil.getData(refreshKey);

		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
		}

		// 새로운 액세스 토큰 발급
		String newAccessToken = jwtProvider.generateToken(userId);
		log.info("New access token issued for user {}", userId);

		return newAccessToken;
	}
}

package com.example.screen_golf.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.jwts.JwtProvider;
import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;
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

	private static final String LOGIN_STATUS_PREFIX = "login:status:";
	private static final long LOGIN_STATUS_EXPIRATION = 24 * 60 * 60 * 1000; // 24시간

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

			User user = User.builder()
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.name(request.getName())
				.phone(request.getPhone())
				.profileImage(request.getProfileImage())
				.role(UserRole.USER)
				.status(UserStatus.ACTIVE)
				.build();
			log.info("생성된 User 객체 - ID: {}, 이메일: {}", user.getId(), user.getEmail());

			User savedUser = userRepository.save(user);
			String generateToken = jwtProvider.generateToken(savedUser.getId());
			log.info("저장된 User 객체 - ID: {}, 이메일: {}", savedUser.getId(), savedUser.getEmail());

			UserSignUpInfo.UserSignUpResponse signUpResponse = UserSignUpInfo.UserSignUpResponse.builder()
				.userId(savedUser.getId())
				.email(savedUser.getEmail())
				.name(savedUser.getName())
				.role(savedUser.getRole())
				.token(generateToken)
				.build();
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

			// 1. 사용자 조회
			User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

			UserLookUpId.UserLookUpIdResponse findResponseUser
				= UserLookUpId.UserLookUpIdResponse.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.phone(user.getPhone())
				.profileImage(user.getProfileImage())
				.role(user.getRole())
				.status(user.getStatus())
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.build();

			log.info("회원 정보 조회 완료 - 사용자 ID: {}", findResponseUser.getUserId());
			return findResponseUser;
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

			UserLookUpName.UserLookUpNameResponse findNameUserResponse
				= UserLookUpName.UserLookUpNameResponse.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.phone(user.getPhone())
				.profileImage(user.getProfileImage())
				.role(user.getRole())
				.status(user.getStatus())
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.build();

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
	@Transactional(readOnly = true)
	public UserLoginInfo.UserLoginResponse login(UserLoginInfo.UserLoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 다릅니다.");
		}

		String generateToken = jwtProvider.generateToken(user.getId());

		// 로그인 상태를 Redis에 저장
		String loginKey = LOGIN_STATUS_PREFIX + user.getId();
		redisUtil.setDataExpire(loginKey, "ACTIVE", LOGIN_STATUS_EXPIRATION);

		log.info("User {} logged in successfully", user.getId());
		return new UserLoginInfo.UserLoginResponse((user.getId()), user.getEmail(), generateToken);
	}

	@Override
	public boolean isUserLoggedIn(Long userId) {
		String loginKey = LOGIN_STATUS_PREFIX + userId;
		return redisUtil.hasKey(loginKey);
	}

	@Override
	public void logout(Long userId) {
		String loginKey = LOGIN_STATUS_PREFIX + userId;
		redisUtil.deleteData(loginKey);
		log.info("User {} logged out", userId);
	}
}

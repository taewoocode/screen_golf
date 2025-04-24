package com.example.screen_golf.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	@Override
	@Transactional
	public User.UserSignUpResponse registerUser(User.UserSignUpRequest request) {
		try {
			log.info("회원가입 요청 시작 - 이메일: {}", request.getEmail());

			// 1. 이메일 중복 체크
			if (userRepository.existsByEmail(request.getEmail())) {
				log.warn("이미 존재하는 이메일: {}", request.getEmail());
				throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
			}

			// 2. User 객체 생성
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

			// 3. User 저장
			User savedUser = userRepository.save(user);
			log.info("저장된 User 객체 - ID: {}, 이메일: {}", savedUser.getId(), savedUser.getEmail());

			// 4. 응답 생성
			User.UserSignUpResponse response = User.UserSignUpResponse.builder()
				.userId(savedUser.getId())
				.email(savedUser.getEmail())
				.name(savedUser.getName())
				.role(savedUser.getRole())
				.build();
			log.info("회원가입 완료 - 사용자 ID: {}", response.getUserId());

			return response;
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
	public User.UserInfoResponse findUser(User.UserInfoRequest request) {
		try {
			log.info("회원 정보 조회 시작 - 사용자 ID: {}", request.getUserId());

			// 1. 사용자 조회
			User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

			// 2. 응답 생성
			User.UserInfoResponse response = User.UserInfoResponse.builder()
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

			log.info("회원 정보 조회 완료 - 사용자 ID: {}", response.getUserId());
			return response;
		} catch (IllegalArgumentException e) {
			log.error("회원 정보 조회 실패 (유효성 검사) - 사용자 ID: {}", request.getUserId(), e);
			throw e;
		} catch (Exception e) {
			log.error("회원 정보 조회 실패 (서버 오류) - 사용자 ID: {}", request.getUserId(), e);
			throw new RuntimeException("회원 정보 조회 중 오류가 발생했습니다.", e);
		}
	}
}

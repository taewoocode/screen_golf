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
	 * 회원가
	 * @param request
	 * @return
	 */
	@Override
	@Transactional
	public User.UserSignUpResponse registerUser(User.UserSignUpRequest request) {
		try {
			log.info("회원가입 요청 시작 - 이메일={}", request.getEmail());

			if (userRepository.existsByEmail(request.getEmail())) {
				log.warn("이미 존재하는 이메일={}", request.getEmail());
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
			log.info("생성된 User 객체 - ID: {}, 이메일={}", user.getId(), user.getEmail());

			User savedUser = userRepository.save(user);
			log.info("저장된 User 객체 - ID: {}, 이메일={}", savedUser.getId(), savedUser.getEmail());

			User.UserSignUpResponse response = User.UserSignUpResponse.builder()
				.userId(savedUser.getId())
				.email(savedUser.getEmail())
				.name(savedUser.getName())
				.role(savedUser.getRole())
				.build();
			log.info("회원가입 완료 - 사용자 ID={}", response.getUserId());

			return response;
		} catch (IllegalArgumentException e) {
			log.error("회원가입 실패 (유효성 검사) - 이메일={}", request.getEmail(), e);
			throw e;
		} catch (Exception e) {
			log.error("회원가입 실패 (서버 오류) - 이메일={}", request.getEmail(), e);
			throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
		}
	}
}

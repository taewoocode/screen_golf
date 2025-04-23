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

	@Override
	@Transactional
	public User.UserSignUpResponse registerUser(User.UserSignUpRequest request) {
		User user = User.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.name(request.getName())
			.phone(request.getPhone())
			.profileImage(request.getProfileImage())
			.role(UserRole.USER)
			.status(UserStatus.ACTIVE)
			.build();

		User savedUser = userRepository.save(user);

		return User.UserSignUpResponse.builder()
			.userId(savedUser.getId())
			.email(savedUser.getEmail())
			.name(savedUser.getName())
			.role(savedUser.getRole())
			.build();
	}
}

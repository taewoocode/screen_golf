package com.example.screen_golf.user.dto;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserRole;
import com.example.screen_golf.user.domain.UserStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserConverter {

	private PasswordEncoder passwordEncoder;

	public User makeEntity(UserSignUpInfo.UserSignUpRequest request) {
		return User.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.name(request.getName())
			.phone(request.getPhone())
			.profileImage(request.getProfileImage())
			.role(UserRole.USER)
			.status(UserStatus.ACTIVE)
			.build();
	}

	public UserSignUpInfo.UserSignUpResponse makeSignUpResponse(User savedUser, String generateToken) {
		return UserSignUpInfo.UserSignUpResponse.builder()
			.userId(savedUser.getId())
			.email(savedUser.getEmail())
			.name(savedUser.getName())
			.role(savedUser.getRole())
			.token(generateToken)
			.build();
	}

	public UserLookUpId.UserLookUpIdResponse makeFindUserEntity(User user) {
		return UserLookUpId.UserLookUpIdResponse.builder()
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
	}

	public UserLookUpName.UserLookUpNameResponse makeUserLookUpNameEntity(User user) {
		return UserLookUpName.UserLookUpNameResponse.builder()
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
	}
}

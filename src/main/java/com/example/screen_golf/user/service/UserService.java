package com.example.screen_golf.user.service;

import com.example.screen_golf.user.domain.User;

public interface UserService {

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	User.UserSignUpResponse registerUser(User.UserSignUpRequest request);

}

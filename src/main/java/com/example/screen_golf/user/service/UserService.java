package com.example.screen_golf.user.service;

import com.example.screen_golf.user.domain.User;

public interface UserService {

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	User.UserSignUpResponse registerUser(User.UserSignUpRequest request);

	/**
	 * 유저 정보 가져오기
	 * @param request
	 * @return
	 */
	User.UserInfoResponse findUser(User.UserInfoRequest request);

}

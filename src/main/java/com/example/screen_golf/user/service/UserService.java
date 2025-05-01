package com.example.screen_golf.user.service;

import com.example.screen_golf.user.dto.UserLoginInfo;
import com.example.screen_golf.user.dto.UserLookUpId;
import com.example.screen_golf.user.dto.UserLookUpName;
import com.example.screen_golf.user.dto.UserSignUpInfo;

public interface UserService {

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	UserSignUpInfo.UserSignUpResponse registerUser(UserSignUpInfo.UserSignUpRequest request);

	/**
	 * 유저 정보 가져오기
	 * @param request
	 * @return
	 */
	UserLookUpId.UserLookUpIdResponse findUser(UserLookUpId.UserLookUpIdRequest request);

	/**
	 * 이름으로 회원정보 조회하기
	 * @param request
	 * @return
	 */
	UserLookUpName.UserLookUpNameResponse findUser(UserLookUpName.UserLookUpNameRequest request);

	/**
	 * email, password login
	 * @param request
	 * @return
	 */
	UserLoginInfo.UserLoginResponse login(UserLoginInfo.UserLoginRequest request);

}

package com.example.screen_golf.user.service;

import com.example.screen_golf.user.domain.User;

public interface UserService {

	User.UserSignUpResponse registerUser(User.UserSignUpRequest request);
}

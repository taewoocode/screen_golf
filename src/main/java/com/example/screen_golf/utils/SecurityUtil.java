package com.example.screen_golf.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.screen_golf.jwts.CustomUserDetails;

public class SecurityUtil {
	public static Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
			throw new RuntimeException("로그인한 사용자가 아닙니다.");
		}

		return ((CustomUserDetails)authentication.getPrincipal()).getUserId();
	}
} 
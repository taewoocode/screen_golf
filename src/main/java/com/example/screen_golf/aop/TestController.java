package com.example.screen_golf.aop;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

	@GetMapping("/error")
	public ResponseEntity<String> triggerError() {
		// 강제로 예외를 던집니다.
		throw new RuntimeException("Test exception for error notification.");
	}
}

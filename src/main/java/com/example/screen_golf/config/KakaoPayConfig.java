package com.example.screen_golf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KakaoPayConfig {

	@Value("${kakaopay.client-id}")
	private String clientId;

	@Value("${kakaopay.client-secret}")
	private String clientSecret;

	@Value("${kakaopay.secret-key}")
	private String secretKey;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

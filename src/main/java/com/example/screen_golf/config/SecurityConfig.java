package com.example.screen_golf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			/**
			 * Swagger 권한 설정
			 * Spring Security 6에서는 SecurityFilterChain 사용 권장
			 * Security -> Swagger 권한이 필요함
			 */
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(authz -> authz
				.requestMatchers(
					"/swagger-resources/**",
					"/swagger-ui/**",
					"/v2/api-docs",
					"/v3/api-docs/**",
					"/webjars/**",
					"/api/products"
				).permitAll()
				.anyRequest().authenticated()
			);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

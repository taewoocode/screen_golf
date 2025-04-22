package com.example.screen_golf.utils;

import java.security.AuthProvider;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1시간
	private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

	public String generateToken(Long id, String email, AuthProvider provider) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("email", email);
		claims.put("provider", provider);

		return Jwts.builder()
			.setClaims(new HashMap<>())
			.setSubject(String.valueOf(id))
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public Map<String, Object> extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public String generateRefreshToken(Long id) {
		return Jwts.builder()
			.setSubject(id.toString())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	// JWT 검증 및 정보 추출
	public String extractId(String token) {
		return getClaims(token).getSubject();
	}

	public boolean validateToken(String token) {
		try {
			getClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims getClaims(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody();
	}

}


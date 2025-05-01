package com.example.screen_golf.jwts;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	private final Key key;
	private final long expiration;

	public JwtProvider(@Value("${GOLF_JWT_SECRET}") String secretKey,
		@Value("${GOLF_JWT_EXPIRATION}") long expiration) {
		this.key = secretKey.length() >= 256 ? Keys.hmacShaKeyFor(secretKey.getBytes())
			: Keys.secretKeyFor(SignatureAlgorithm.HS256);
		this.expiration = expiration;
	}

	/**
	 * GenerationToken
	 * @param userId
	 * @return
	 */
	public String generateToken(Long userId) {
		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(now)
			.setExpiration(expirationDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// 토큰에서 사용자 ID 추출
	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject());
	}

	// 토큰 유효성 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}

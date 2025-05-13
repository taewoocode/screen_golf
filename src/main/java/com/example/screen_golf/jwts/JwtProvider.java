package com.example.screen_golf.jwts;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-expiration}")
	private long refreshTokenExpiration;

	private SecretKey secretKeyInstance;

	@PostConstruct
	protected void init() {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			try {
				MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
				keyBytes = sha256.digest(keyBytes);
				log.warn("주입받은 비밀 키가 32바이트보다 짧아 SHA-256 해시를 사용해 변환");
			} catch (NoSuchAlgorithmException e) {
				log.error("SHA-256 알고리즘을 사용할 수 없습니다.", e);
				throw new RuntimeException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
			}
		}
		secretKeyInstance = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Long userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(secretKeyInstance)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(secretKeyInstance)
			.compact();
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(secretKeyInstance)
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKeyInstance)
				.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			log.error("JWT 검증 오류: {}", e.getMessage());
			return false;
		}
	}

	public long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}
}
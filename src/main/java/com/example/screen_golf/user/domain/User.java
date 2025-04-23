package com.example.screen_golf.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.screen_golf.reservation.domain.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	private String profileImage;

	@OneToMany(mappedBy = "user")
	private List<Reservation> reservations = new ArrayList<>();

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public User(String email, String password, String name, String phone, UserRole role, UserStatus status, String profileImage) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.role = role;
		this.status = status;
		this.profileImage = profileImage;
	}

	public void updateProfile(String name, String phone, String profileImage) {
		this.name = name;
		this.phone = phone;
		this.profileImage = profileImage;
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}

	/** ########## 회원가입 DTO ########## **/
	@Getter
	@NoArgsConstructor
	public static class UserSignUpRequest {
		private String email;
		private String password;
		private String name;
		private String phone;
		private String profileImage;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UserSignUpResponse {
		private Long userId;
		private String email;
		private String name;
		private UserRole role;
	}
}
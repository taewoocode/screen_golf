package com.example.screen_golf.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.user.domain.User;
import com.example.screen_golf.user.domain.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findByName(String name);

	/**
	 * 활성화된 유저 조회
	 * @return
	 */
	// List<User> findAllByActiveTrue();

	List<User> findAllByStatus(UserStatus status);

}

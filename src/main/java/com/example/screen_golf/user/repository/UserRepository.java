package com.example.screen_golf.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

package com.example.screen_golf.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.user.domain.User;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
	Optional<UserCoupon> findByUser(User user);
}

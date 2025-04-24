package com.example.screen_golf.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.coupon.domain.UserCoupon;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
}

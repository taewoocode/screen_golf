package com.example.screen_golf.coupon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.coupon.domain.CouponStatus;
import com.example.screen_golf.coupon.domain.UserCoupon;
import com.example.screen_golf.user.domain.User;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
	Optional<UserCoupon> findByUser(User user);

	@Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = :status " +
		"AND uc.validFrom <= :now AND uc.validTo >= :now")
	List<UserCoupon> findAvailableCoupons(@Param("userId") Long userId,
		@Param("status") CouponStatus status,
		@Param("now") LocalDateTime now);
}

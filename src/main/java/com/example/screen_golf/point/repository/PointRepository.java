package com.example.screen_golf.point.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.screen_golf.point.domain.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
	@Query("SELECT SUM(p.amount) FROM Point p WHERE p.user.id = :userId")
	Integer sumAmountByUserId(@Param("userId") Long userId);

	List<Point> findByUserIdOrderByCreatedAtDesc(Long userId);
}

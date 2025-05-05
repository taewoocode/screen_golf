package com.example.screen_golf.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.screen_golf.point.domain.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
}

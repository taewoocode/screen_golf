package com.example.screen_golf.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.room.domain.RoomPrice;

@Repository
public interface RoomPriceRepository extends JpaRepository<RoomPrice, Long> {

	Optional<RoomPrice> findByRoom(Long RoomPriceId);
}

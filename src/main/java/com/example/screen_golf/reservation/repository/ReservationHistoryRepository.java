package com.example.screen_golf.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.reservation.domain.Reservation;
import com.example.screen_golf.reservation.domain.ReservationHistory;
import com.example.screen_golf.reservation.domain.ReservationStatus;

@Repository
public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {
    List<ReservationHistory> findByReservation(Reservation reservation);
    List<ReservationHistory> findByNewStatus(ReservationStatus status);
} 
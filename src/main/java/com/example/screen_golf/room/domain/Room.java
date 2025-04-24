package com.example.screen_golf.room.domain;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column(nullable = false)
    private Integer pricePerHour;

    private String description;

    @OneToMany(mappedBy = "room")
    private List<Reservation> reservations = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Room(String name, RoomStatus status, Integer pricePerHour, String description) {
        this.name = name;
        this.status = status;
        this.pricePerHour = pricePerHour;
        this.description = description;
    }

    public void updateRoomInfo(String name, Integer pricePerHour, String description) {
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.description = description;
    }

    public void changeStatus(RoomStatus status) {
        this.status = status;
    }
} 
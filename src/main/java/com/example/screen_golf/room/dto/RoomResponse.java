package com.example.screen_golf.room.dto;

import com.example.screen_golf.room.domain.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private RoomType roomType;
    private Integer pricePerHour;
    private String description;
    private Integer userCount;
} 
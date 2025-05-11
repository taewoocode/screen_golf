package com.example.screen_golf.point.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

public class PointInfo {

    @Getter
    @Builder
    public static class CurrentPointResponse {
        private final int currentPoint;
    }

    @Getter
    @Builder
    public static class PointCheckRequest {
        private final int useAmount;
    }

    @Getter
    @Builder
    public static class PointCheckResponse {
        private final boolean isAvailable;
    }

    @Getter
    @Builder
    public static class PointUseRequest {
        private final int useAmount;
    }

    @Getter
    @Builder
    public static class PointUseResponse {
        private final Long userId;
        private final int useAmount;
        private final LocalDateTime usedAt;
    }

    @Getter
    @Builder
    public static class PointHistoryResponse {
        private final Long pointId;
        private final Long userId;
        private final int amount;
        private final LocalDateTime createdAt;
    }
} 
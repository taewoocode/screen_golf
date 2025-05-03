package com.example.screen_golf.room.domain;

import lombok.Getter;

@Getter
public enum RoomType {
	STANDARD(5000),
	PREMIUM(10000),
	VIP(20000);

	private final int pricePerHour;

	RoomType(int pricePerHour) {
		this.pricePerHour = pricePerHour;
	}
}
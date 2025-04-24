package com.example.screen_golf.exception.room;

public class RoomNotFoundException extends RuntimeException {
	public RoomNotFoundException() {
		super();
	}

	public RoomNotFoundException(String message) {
		super(message);
	}

	public RoomNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoomNotFoundException(Throwable cause) {
		super(cause);
	}
}

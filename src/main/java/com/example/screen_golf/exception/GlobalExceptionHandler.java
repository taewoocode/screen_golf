package com.example.screen_golf.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.screen_golf.exception.reservation.ReservationConflictException;
import com.example.screen_golf.exception.reservation.ReservationValidationException;
import com.example.screen_golf.exception.reservation.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error("유효성 검사 실패: {}", e.getMessage(), e);
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Void> handleException(Exception e) {
		log.error("서버 오류 발생: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**=========================================================================
	 * 							Reservation 관련
	 * =========================================================================
	 */

	@ExceptionHandler(ReservationValidationException.class)
	public ResponseEntity<String> handleReservationValidationException(ReservationValidationException e) {
		log.error("예약 유효성 검사 실패: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	@ExceptionHandler(ReservationConflictException.class)
	public ResponseEntity<String> handleReservationConflictException(ReservationConflictException e) {
		log.error("예약 충돌 발생: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
		log.error("리소스 조회 실패: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
}
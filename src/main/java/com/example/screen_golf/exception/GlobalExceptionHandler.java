package com.example.screen_golf.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.screen_golf.exception.reservation.ReservationConflictException;
import com.example.screen_golf.exception.reservation.ReservationValidationException;
import com.example.screen_golf.exception.reservation.ResourceNotFoundException;
import com.example.screen_golf.exception.room.RoomCreateException;
import com.example.screen_golf.exception.room.RoomNotFoundException;
import com.example.screen_golf.exception.room.RoomStateException;
import com.example.screen_golf.exception.room.RoomTimeException;
import com.example.screen_golf.exception.room.RoomUpdateException;

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

	/**=========================================================================
	 *                           Room 관련
	 * =========================================================================
	 */

	// 예: Room을 조회하지 못했을 때 발생하는 예외 처리 (RoomNotFoundException)
	@ExceptionHandler(RoomNotFoundException.class)
	public ResponseEntity<String> handleRoomNotFoundException(RoomNotFoundException e) {
		log.error("Room 조회 실패: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}

	// 예: Room의 상태나 비즈니스 로직 관련 예외가 있다면 추가 핸들러 작성 가능
	@ExceptionHandler(RoomStateException.class)
	public ResponseEntity<String> handleRoomStateException(RoomStateException e) {
		log.error("Room 상태 오류: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	/**
	 * @Valid 실패 시 (400 Bad Request)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
		String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		log.warn("유효성 검사 실패: {}", errorMessage);
		return ResponseEntity.badRequest().body(errorMessage);
	}

	/**
	 * Room 생성 시간 예외처리
	 * @param e
	 * @return
	 */
	@ExceptionHandler(RoomTimeException.class)
	public ResponseEntity<String> handleRoomTimeException(RoomTimeException e) {
		log.error("Room 시간 검증 실패: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	/**
	 * 형 변환 실패 (400 Bad Request)
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
		log.warn("형 변환 실패: {}", e.getMessage());
		return ResponseEntity.badRequest().body("잘못된 요청 파라미터 형식입니다.");
	}

	/**
	 * Room 생성 실패 핸들러
	 * @param e
	 * @return
	 */
	@ExceptionHandler(RoomCreateException.class)
	public ResponseEntity<String> handleRoomCreateException(RoomCreateException e) {
		log.error("Room 생성 오류: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	/**
	 * Room Update 오류 핸들러
	 * @param e
	 * @return
	 */
	@ExceptionHandler(RoomUpdateException.class)
	public ResponseEntity<String> handleRoomUpdateException(RoomUpdateException e) {
		log.error("Room 업데이트 오류: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
}
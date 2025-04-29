package com.example.screen_golf.swagger;

public class SwaggerDocs {

	/**===================================================================
	 *					 User 관련 Swagger Docs
	 * ===================================================================
	 */
	public static final String SUMMARY_USER_SIGNUP = "회원가입";
	public static final String DESCRIPTION_USER_SIGNUP = "이메일, 비밀번호, 이름, 전화번호, 프로필 이미지를 입력하여 회원가입을 진행합니다.";

	public static final String SUMMARY_USER_INFO = "회원 정보 조회";
	public static final String DESCRIPTION_USER_INFO = "사용자 ID를 통해 회원 정보를 조회합니다.";

	public static final String SUMMARY_USER_INFO_BY_NAME = "이름으로 회원 정보 조회";
	public static final String DESCRIPTION_USER_INFO_BY_NAME = "사용자 이름을 통해 회원 정보를 조회합니다.";

	/**===================================================================
	 *					 Reservation 관련 Swagger Docs
	 * ===================================================================
	 */
	public static final String SUMMARY_RESERVATION_CREATE = "예약 생성";
	public static final String DESCRIPTION_RESERVATION_CREATE =
		"예약 진행 요청 DTO를 입력받아 예약 가능한 방을 선택한 후, 예약을 생성합니다. " +
			"운영시간(11:00 ~ 22:00) 내에 예약이 진행되어야 하며, 겹치는 예약이 있는 경우 에러를 반환합니다.";

	public static final String SUMMARY_RESERVATION_SEARCH_AVAILABLE = "예약 가능한 방 검색";
	public static final String DESCRIPTION_RESERVATION_SEARCH_AVAILABLE =
		"특정 날짜, 예약 시작/종료 시간, 그리고 원하는 룸 타입(STANDARD, PREMIUM, VIP)을 입력하여 " +
			"예약 가능한 방의 목록을 조회합니다. 운영시간 내 검색이 진행됩니다.";

	public static final String SUMMARY_RESERVATION_GET_USER_RESERVATIONS = "사용자 예약 내역 조회";
	public static final String DESCRIPTION_RESERVATION_GET_USER_RESERVATIONS =
		"특정 사용자 ID를 기준으로 해당 사용자의 예약 내역 목록을 조회합니다.";

	/**===================================================================
	 *                   Room 관련 Swagger Docs
	 * ===================================================================
	 */
	public static final String SUMMARY_ROOM_LIST = "전체 Room 목록 조회";
	public static final String DESCRIPTION_ROOM_LIST =
		"이용 가능, 예약 중, 사용 중 등 모든 Room의 상세 정보를 조회합니다.";

	public static final String SUMMARY_ROOM_BY_TYPE = "Room 타입으로 조회";
	public static final String DESCRIPTION_ROOM_BY_TYPE =
		"RoomType 요청 DTO를 입력받아 해당 타입에 해당하는 Room 목록을 조회합니다.";

	public static final String SUMMARY_ROOM_DETAIL = "Room 상세 조회";
	public static final String DESCRIPTION_ROOM_DETAIL =
		"Room ID를 기반으로 특정 Room의 상세 정보를 조회합니다.";

	public static final String SUMMARY_ROOM_CREATE = "Room 생성";
	public static final String DESCRIPTION_CREATE_ROOM =
		"Room ID를 기반으로 특정 Room의 상세 정보를 조회합니다.";

	// Room 상태 변경
	public static final String SUMMARY_ROOM_STATUS_CHANGE = "Room 상태 변경";
	public static final String DESCRIPTION_ROOM_STATUS_CHANGE = "Room의 상태를 변경합니다.";

	// Room 삭제
	public static final String SUMMARY_ROOM_DELETE = "Room 삭제";
	public static final String DESCRIPTION_ROOM_DELETE = "Room을 삭제합니다.";

	/**===================================================================
	 *                   UserCoupon 관련 Swagger Docs
	 * ===================================================================
	 */
	public static final String SUMMARY_CREATE_USER_COUPON = "사용자 쿠폰 생성";
	public static final String DESCRIPTION_CREATE_USER_COUPON =
		"사용자 ID와 쿠폰 정보를 입력받아 사용자에게 쿠폰을 발급합니다.";

	public static final String SUMMARY_DELETE_USER_COUPON = "사용자 쿠폰 삭제";
	public static final String DESCRIPTION_DELETE_USER_COUPON =
		"CouponId를 입력받아 쿠폰을 삭제합니다.";

	/**===================================================================
	 *                   Payment 관련 Swagger Docs
	 * ===================================================================
	 */
	public static final String SUMMARY_REQUEST_PAYMENT = "결제 요청";
	public static final String DESCRIPTION_REQUEST_PAYMENT =
		"예약 ID, 사용자 ID, 결제 금액, 결제 수단 정보를 입력받아 결제 요청을 진행합니다.";
}
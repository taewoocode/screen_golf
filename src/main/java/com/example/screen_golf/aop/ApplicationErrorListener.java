package com.example.screen_golf.aop;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.screen_golf.notification.service.DiscordNotificationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationErrorListener {

	private final ApplicationContext applicationContext;
	private DiscordNotificationService discordNotificationService;

	@PostConstruct
	public void init() {
		log.info("ApplicationErrorListener 초기화 시작");
		try {
			this.discordNotificationService = applicationContext.getBean(DiscordNotificationService.class);
			log.info("DiscordNotificationService 초기화 성공");
		} catch (Exception e) {
			log.error("DiscordNotificationService 초기화 실패", e);
		}
	}

	/**
	 * 애플리케이션 시작 실패 시 에러를 디스코드로 전송
	 */
	@EventListener(ApplicationFailedEvent.class)
	public void handleApplicationFailure(ApplicationFailedEvent event) {
		Throwable exception = event.getException();
		String errorMessage = String.format("""
				💥 애플리케이션 시작 실패
				에러: %s
				스택트레이스:
				%s
				""",
			exception.getMessage(),
			getStackTraceAsString(exception)
		);

		log.error(errorMessage);

		// DiscordNotificationService가 null일 경우 다시 초기화 시도
		try {
			if (discordNotificationService == null) {
				log.error("DiscordNotificationService가 null입니다. 다시 초기화를 시도합니다.");
				discordNotificationService = applicationContext.getBean(DiscordNotificationService.class);
			}

			// 알림 전송 시 예외 처리
			discordNotificationService.sendErrorNotification(errorMessage, getStackTraceAsString(exception));
			log.info("디스코드 알림 전송 성공");
		} catch (Exception e) {
			log.error("디스코드 알림 전송 실패", e);
		}
	}

	private String getStackTraceAsString(Throwable throwable) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : throwable.getStackTrace()) {
			sb.append(element.toString()).append("\n");
		}
		return sb.toString();
	}
}

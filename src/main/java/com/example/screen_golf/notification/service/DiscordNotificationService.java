package com.example.screen_golf.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {
	private final RestTemplate restTemplate;

	@Value("${logging.discord.webhook-uri}")
	private String webhookUri;

	/**
	 * 메시지를 Discord로 전송 -> Embed로 format
	 */
	public void sendInfoNotification(String logMessage) {
		String formattedMessage = logMessage.length() > 1900 ? logMessage.substring(0, 1900) + "..." : logMessage;
		sendEmbedNotification("Log Notification", formattedMessage);
	}

	/**
	 * 에러 발생 알림을 디스코드로 전송하는 메서드
	 */
	public void sendErrorNotification(String errorMessage, String stackTrace) {
		// 에러 알림 메시지 생성
		String formattedMessage = String.format("💥 에러 발생: %s", errorMessage);
		sendEmbedNotification(formattedMessage, stackTrace);
	}

	/**
	 * 임베드를 사용하여 공통 알림 전송
	 */
	private void sendEmbedNotification(String title, String description) {
		String formattedTitle = title.length() > 1900 ? title.substring(0, 1900) + "..." : title;
		String formattedDescription =
			description.length() > 1900 ? description.substring(0, 1900) + "..." : description;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String payload = String.format("""
				{
				    "embeds": [
				        {
				            "title": "%s",
				            "description": "%s",
				            "color": 16711680,
				            "footer": {
				                "text": "Notification"
				            }
				        }
				    ]
				}
				""", escapeJson(formattedTitle), escapeJson(formattedDescription));

			HttpEntity<String> request = new HttpEntity<>(payload, headers);
			restTemplate.postForObject(webhookUri, request, String.class);

			log.info("Discord embed notification sent successfully");
		} catch (Exception e) {
			log.error("Failed to send Discord notification: {}", e.getMessage());
		}
	}

	// JSON 문자열에서 특수문자를 이스케이프 처리
	private String escapeJson(String input) {
		return input.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	public void sendPaymentNotification(String notificationMessage) {
		sendInfoNotification("💰 결제 성공: " + notificationMessage);
	}
}

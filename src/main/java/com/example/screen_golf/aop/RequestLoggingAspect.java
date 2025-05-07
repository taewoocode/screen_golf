package com.example.screen_golf.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.screen_golf.notification.service.DiscordNotificationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class RequestLoggingAspect {

	private final DiscordNotificationService discordNotificationService;
	private static final ThreadLocal<Boolean> isNotificationSent = ThreadLocal.withInitial(() -> false);

	public RequestLoggingAspect(DiscordNotificationService discordNotificationService) {
		this.discordNotificationService = discordNotificationService;
	}

	@Around("(execution(* com.example.screen_golf..*Controller.*(..)) || " +
		"execution(* com.example.screen_golf..*Service.*(..))) && " +
		"!within(com.example.screen_golf.config..*) && " +
		"!within(com.example.screen_golf.jwts..*) && " +
		"!within(com.example.screen_golf.common.security..*) && " +
		"!within(com.example.screen_golf.notification.service.DiscordNotificationService)")
	public Object doLogging(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		Map<String, String[]> paramMap = request.getParameterMap();
		String params = paramMap.isEmpty() ? "" : "[" + paramMapToString(paramMap) + "]";

		String methodName = pjp.getSignature().toShortString();
		long start = System.currentTimeMillis();

		// 로그 누적용 버퍼
		StringBuilder logBuffer = new StringBuilder();
		logBuffer.append(
			"=================================================================================================\n");
		logBuffer.append(" ==> Start: ").append(methodName).append("\n");

		try {
			Object result = pjp.proceed(pjp.getArgs());

			long end = System.currentTimeMillis();
			String logMsg = String.format("Request: %s %s %s < %s (%dms)",
				request.getMethod(),
				request.getRequestURI(),
				params,
				request.getRemoteHost(),
				end - start
			);

			logBuffer.append(logMsg).append("\n");
			logBuffer.append(" ==> End: ").append(methodName).append("\n");
			logBuffer.append(
				"=================================================================================================\n");

			log.info(logBuffer.toString());

			if (!isNotificationSent.get()) {
				// 디스코드 알림 전송
				discordNotificationService.sendInfoNotification(logBuffer.toString());
				isNotificationSent.set(true);  // 알림을 보냈으므로 상태를 true로 변경
			}

			return result;
		} catch (Exception e) {
			String errorMessage = String.format("Request failed - %s.%s with params: %s",
				pjp.getSignature().getDeclaringTypeName(),
				pjp.getSignature().getName(),
				params);

			log.error(errorMessage, e);

			// 디스코드 에러 알림을 한 번만 전송
			if (!isNotificationSent.get()) {
				discordNotificationService.sendErrorNotification(errorMessage, getStackTraceAsString(e));
				isNotificationSent.set(true);  // 알림을 보냈으므로 상태를 true로 변경
			}
			throw e;  // 예외를 다시 던져서 상위 계층에서 처리하도록
		}
	}

	private String paramMapToString(Map<String, String[]> paramMap) {
		return paramMap.entrySet().stream()
			.map(entry -> {
				String key = entry.getKey();
				String value = String.join(",", entry.getValue());
				return String.format("%s -> (%s)", key, value);
			})
			.collect(Collectors.joining(", "));
	}

	private String getStackTraceAsString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

}

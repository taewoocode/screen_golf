package com.example.screen_golf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
	"com.example.screen_golf.community.repository.jpa",
	"com.example.screen_golf.user.repository",
	"com.example.screen_golf.coupon.repository",
	"com.example.screen_golf.payment.repository",
	"com.example.screen_golf.room.repository",
	"com.example.screen_golf.reservation.repository",
	"com.example.screen_golf.point.repository",
})
public class JpaConfig {
}

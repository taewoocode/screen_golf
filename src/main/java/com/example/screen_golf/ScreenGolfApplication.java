package com.example.screen_golf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class ScreenGolfApplication {
	public static void main(String[] args) {
		SpringApplication.run(ScreenGolfApplication.class, args);
	}
}

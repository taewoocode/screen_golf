package com.example.screen_golf.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.screen_golf.reservation.service.DiscountPolicy;
import com.example.screen_golf.reservation.service.RateAmountCountPolicy;

@Configuration
public class DiscountPolicyConfig {

	// @Bean
	// public DiscountPolicy fixedAmountDiscountPolicy() {
	// 	return new FixedAmountCountPolicy();
	// }

	@Bean
	public DiscountPolicy rateAmountDiscountPolicy() {
		return new RateAmountCountPolicy();
	}
}

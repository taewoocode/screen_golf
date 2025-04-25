package com.example.screen_golf.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.screen_golf.reservation.service.DiscountPolicy;
import com.example.screen_golf.reservation.service.FixedAmountCountPolicy;
import com.example.screen_golf.reservation.service.RateAmountCountPolicy;

@Configuration
public class DiscountPolicyConfig {

	@Bean
	public DiscountPolicy fixedAmountDiscountPolicy() {
		return new FixedAmountCountPolicy();
	}

	@Bean
	@Primary
	public DiscountPolicy rateAmountDiscountPolicy() {
		return new RateAmountCountPolicy();
	}
}

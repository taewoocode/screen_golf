package com.example.screen_golf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Screen Golf API")
				.description("Screen Golf Management System API Documentation")
				.version("1.0.0")
				.license(new License()
					.name("Apache 2.0")
					.url("https://www.apache.org/licenses/LICENSE-2.0")
				)
			)
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components()
				.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
			);
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}
}

package com.scai.entities.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
	@Bean
	protected SecurityFilterChain filerChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(httpRequest -> httpRequest.anyRequest().authenticated()).httpBasic(withDefaults());
		return http.build();
	}

	@Bean
	protected WebSecurityCustomizer webSecurityCustomizer() {
		return webSecurityCustomizer -> webSecurityCustomizer.ignoring().antMatchers("/v3/api-docs/**",
				"/swagger-ui/**", "/swagger-ui.html", "/actuator/**", "/api/v2/**", "/v3/**", "/entities/v1/**", "/entities/products/v1/**", "/entities/personnels/v1/**");
	}

}

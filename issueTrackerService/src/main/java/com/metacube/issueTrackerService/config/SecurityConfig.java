package com.metacube.issueTrackerService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.metacube.issueTrackerService.filter.JwtFilter;




@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception{
		 http
		 .csrf(AbstractHttpConfigurer::disable)
         .authorizeHttpRequests((requests) -> requests
        		// .requestMatchers("/health-check", "/user/login", "/user/signup").permitAll()
         		.requestMatchers("/tickets/delete/ticketId/**", "/tickets/all-tickets").hasRole("ADMIN")
         		.requestMatchers("/tickets/customer/**").hasRole("CUSTOMER")
         		.requestMatchers("/tickets/support/**").hasRole("SUPPORT")
         		.requestMatchers("/tickets/id/**", "/tickets/ticketId/**", "/tickets/ticketId/**/add-comment").hasAnyRole("ADMIN", "DEVELOPER", "SUPPORT")
         		.requestMatchers("/tickets/get-note/ticketId/*").hasAnyRole("SUPPORT", "DEVELOPER", "ADMIN", "CUSTOMER")
        			             .anyRequest().permitAll()
         )
         .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
     return http.build();
	}
}

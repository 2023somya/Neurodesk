package com.metacube.userRegistrationService.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatchers;
import org.springframework.web.cors.CorsConfiguration;

import com.metacube.userRegistrationService.filter.JwtFilter;
import com.metacube.userRegistrationService.service.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityService {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	
	@Autowired
	private JwtFilter jwtFilter;
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		
		// Define a RequestMatcher for public endpoints (to exclude from Basic Auth)
		
       http
		
        		// Configure CORS globally for all requests
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080", "http://localhost:8081"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // Disable CSRF (stateless API)
               .csrf(AbstractHttpConfigurer::disable)
                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                		.requestMatchers("/health-check", "/user/login", "/user/signup").permitAll()
                		.requestMatchers("/user/allUsers", "/user/email/**").hasAnyRole("ADMIN", "CUSTOMER", "DEVELOPER", "SUPPORT")
                	//	.requestMatchers("/user/email/**").permitAll()
                    // Permit public endpoints without authentication
                   // .requestMatchers("/user/add", "/auth/**", "/api/health-check").permitAll()
                    // All other requests require authentication
                    .anyRequest().authenticated())
                
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions, JWT-based
                    )
               
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, ex2) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
            );
                
           //no need after jwt filter added     //.httpBasic(Customizer.withDefaults())
            return http  .build();
				
	}
	
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
  
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
	
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
	
}

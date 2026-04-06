package com.ds.app.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



import com.ds.app.service.impl.MyUserDetailService;


@Configuration

@EnableWebSecurity

@EnableMethodSecurity

public class SecurityConfig {

//	@Autowired
//	private CustomAccessDeniedHandler accessDeniedHandler;

	
	
//	@Autowired
//	private CustomAuthenticationEntryPoint authenticationEntryPoint;
	
	
	

	@Autowired

	private JWTFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .cors(cors -> cors.disable())

	        .exceptionHandling(ex -> ex
	            .accessDeniedHandler(new RestAccessDeniedHandler())
	            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
	        )

	        .sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )

	        .authorizeHttpRequests(auth -> auth

	            // ✅ Swagger Public Access
	            .requestMatchers(
	                    "/v3/api-docs/**",
	                    "/swagger-ui/**",
	                    "/swagger-ui.html",
	                    "/swagger-resources/**",
	                    "/webjars/**"
	            ).permitAll()

	            // ✅ Public APIs
	            .requestMatchers("/finsecure/public/**").permitAll()

	            // ✅ Role Based APIs
	            .requestMatchers("/finsecure/admin/**").hasRole("ADMIN")
	            .requestMatchers("/finsecure/hr/**").hasRole("HR")
	            .requestMatchers("/finsecure/finance/**").hasRole("FINANCE")
	            .requestMatchers("/finsecure/system/**").hasRole("SYSTEM")
	            .requestMatchers("/finsecure/employee/**").hasRole("EMPLOYEE")
	            .requestMatchers("/finsecure/insurance/**")
	            .hasAnyRole("EMPLOYEE", "ADMIN", "FINANCE", "HR")

	            // ✅ MUST BE LAST ALWAYS
	            .anyRequest().authenticated()
	        );

	    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	@Bean

	PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();

	}

	@Bean

	public AuthenticationManager authenticationManager(

			AuthenticationConfiguration config) throws Exception {

		return config.getAuthenticationManager();

	}

}

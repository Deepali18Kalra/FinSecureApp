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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    
//    @Bean
//    @org.springframework.core.annotation.Order(1)
//    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//            .securityMatcher(
//                "/swagger-ui.html",
//                "/swagger-ui/**",
//                "/v3/api-docs/**"
//            )
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//            );
//
//        return http.build();
//    }
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers("/finsecure/public/**").permitAll()
                .requestMatchers("/finsecure/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/finsecure/hr/**").hasAuthority("HR")
                .requestMatchers("/finsecure/finance/**").hasAuthority("FINANCE")
                .requestMatchers("/finsecure/system/**").hasAuthority("SYSTEM")
                .requestMatchers("/finsecure/employee/**").hasAuthority("EMPLOYEE")
                .requestMatchers("/finsecure/insurance/**")
                    .hasAnyAuthority("EMPLOYEE", "ADMIN", "FINANCE", "HR")
                .requestMatchers("/finsecure/reports/**")
                    .hasAnyAuthority("ADMIN", "HR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .cors(cors -> cors.disable())
//            .sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//
//            .authorizeHttpRequests(auth -> auth
//            		
//
//
//.requestMatchers(
//    "/swagger-ui.html",
//    "/swagger-ui/**",
//    "/v3/api-docs/**",
//    "/**/v3/api-docs/**"
//).permitAll()
//
//            		
//                .requestMatchers("/finsecure/public/**").permitAll()
//
//                .requestMatchers("/finsecure/admin/**").hasRole("ADMIN")
//                .requestMatchers("/finsecure/hr/**").hasRole("HR")
//                .requestMatchers("/finsecure/finance/**").hasRole("FINANCE")
//                .requestMatchers("/finsecure/system/**").hasRole("SYSTEM")
//                .requestMatchers("/finsecure/employee/**").hasRole("EMPLOYEE")
//                .requestMatchers("/finsecure/insurance/**")
//                    .hasAnyRole("EMPLOYEE", "ADMIN", "FINANCE", "HR")
//
//                .requestMatchers("/finsecure/reports/**").hasAnyRole("ADMIN", "HR")
//                    .anyRequest().authenticated()
//                   
//                    
//            )
////            .addFilterBefore((request, response, chain) -> {
////
////            	String path = request.getRequestURI();
////
////                // ✅ Skip JWT filter for Swagger
////                if (path.startsWith("/v3/api-docs")
////                    || path.startsWith("/swagger-ui")) {
////                    chain.doFilter(request, response);
////                    return;
////                }
////
////                jwtFilter.doFilter(request, response, chain);
////
////            }, UsernamePasswordAuthenticationFilter.class);
//
//            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//            
//        return http.build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}



//package com.ds.app.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.ds.app.service.MyUserDetailService;
//
//@Configuration
//@EnableWebSecurity
////@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private MyUserDetailService userDetailsService;
//
//    @Autowired
//    private JWTFilter jwtFilter;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//    @Bean
//    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//
//        AuthenticationManagerBuilder builder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        builder.userDetailsService(userDetailsService)
//               .passwordEncoder(passwordEncoder());
//
//        return builder.build();
//    }
//    
//    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .cors(cors -> cors.disable())
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//            )
//            .sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            );
//
//        return http.build();
//    }
//    
//    
//    
//    
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////
////        http.csrf(csrf -> csrf.disable());
////        http.cors(cors -> cors.disable());
////
//////        http.authorizeHttpRequests(auth -> auth
//////                .requestMatchers("/finsecure/public/**").permitAll()
//////
//////                .requestMatchers("/finsecure/admin/**").hasRole("ADMIN")
//////                .requestMatchers("/finsecure/hr/**").hasRole("HR")
//////                .requestMatchers("/finsecure/employee/**").hasRole("EMPLOYEE")
//////                .requestMatchers("/finsecure/common/**").permitAll()
//////
//////                .anyRequest().authenticated()
//////        );
////
////        
////
//////        http.authorizeHttpRequests(auth -> auth
//////        		.anyRequest().permitAll()
//////        		);
////
////
////
////        http.authorizeHttpRequests(auth -> auth
////       
////        .requestMatchers(
////                "/swagger-ui/**",
////                "/v3/api-docs/**",
////                "/v3/api-docs.yaml"
////        ).permitAll());
////
////        
////        http.sessionManagement(session ->
////                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////        );
////
////        //http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
//
//
////package com.ds.app.security;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.crypto.password.NoOpPasswordEncoder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////
////import com.ds.app.service.MyUserDetailService;
////
////@Configuration
////@EnableWebSecurity
////@EnableMethodSecurity
////public class SecurityConfig{
////
////	@Autowired
////	private MyUserDetailService userDetailsService;
////	
////	@Autowired
////	JWTFilter jwtFilter;
////	
////	@Bean
////	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
////		 http.csrf(csrf -> csrf.disable());
////
////		 http.cors(cors->cors.disable());
////		 
////	        http.authorizeHttpRequests(auth -> auth
////	        		.requestMatchers("/finsecure/public/**").permitAll()
////	                .requestMatchers("/finsecure/admin/**").hasAuthority("ADMIN")
////	                .requestMatchers("/finsecure/hr/**").hasAuthority("HR")
////	                .requestMatchers("/finsecure/finance/**").hasAuthority("FINANCE")
////	                .requestMatchers("/finsecure/system/**").hasAuthority("SYSTEM")
////	                .requestMatchers("/finsecure/employee/**").hasAuthority("EMPLOYEE")
////	                .requestMatchers("/finsecure/common/**").permitAll()
////	                .anyRequest().authenticated()
////
////	        		);
////	        
////	        
////	        http.sessionManagement(session ->
////	                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////	        );
////
////	        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
////
////	        return http.build();
////	}
////	
////	@Bean
////	PasswordEncoder passwordEncoder() {
////    return NoOpPasswordEncoder.getInstance();
////}
//
//	
////	@Bean
////	public PasswordEncoder passwordEncoder() {
////	    return NoOpPasswordEncoder.getInstance();
////	}
////	
//	
////	PasswordEncoder passwordEncoder()
////	{
////		return new BCryptPasswordEncoder();
////	}
////	
//	@Bean
//	public AuthenticationManager authenticationManager(
//	        AuthenticationConfiguration config) throws Exception {
//	    return config.getAuthenticationManager();
//	}
//}

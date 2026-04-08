package com.ds.app.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ds.app.jwtutil.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // ✅ No token → continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // ✅ Extract principal name (username)
            String username = jwtUtil.extractUsername(token);

            // ✅ Avoid re-authentication
            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                List<String> roles = jwtUtil.extractRoles(token);

                var authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,      // principal name
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {
            // Invalid token → ignore, request stays unauthenticated
        }

        filterChain.doFilter(request, response);
    }
    
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs");
    }
    
    
    
}




//package com.ds.app.security;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.ds.app.jwtutil.JWTUtil;
//import com.ds.app.service.MyUserDetailService;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JWTFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JWTUtil jwtUtil;
//
//    @Autowired
//    private MyUserDetailService myUserDetailService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.substring(7);
//
//        try {
//            // Extract all claims
//            Claims claims = jwtUtil.extractAllClaims(token);
//
//            String username = claims.getSubject();
//            Integer userId = claims.get("userId", Integer.class);
//            String role = claims.get("role", String.class);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                // Load UserDetails for password check / enabled check
//                var userDetails = myUserDetailService.loadUserByUsername(username);
//
//                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
//
//                    SimpleGrantedAuthority authority =
//                            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(
//                                    userId,   // principal
//                                    null,
//                                    java.util.List.of(authority)
//                            );
//
//                    authToken.setDetails(
//                            new WebAuthenticationDetailsSource().buildDetails(request)
//                    );
//
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println("JWT Validation failed: " + e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
//
//
//
//
////package com.ds.app.security;
////
////import java.io.IOException;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.core.userdetails.UserDetails;
////import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
////import org.springframework.stereotype.Component;
////import org.springframework.web.filter.OncePerRequestFilter;
////
////import com.ds.app.jwtutil.JWTUtil;
////import com.ds.app.service.MyUserDetailService;
////
////import jakarta.servlet.FilterChain;
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
////
////@Component
////public class JWTFilter extends OncePerRequestFilter {
////
////    @Autowired
////    private JWTUtil jwtUtil;
////
////    @Autowired
////    private MyUserDetailService service;
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request,
////                                    HttpServletResponse response,
////                                    FilterChain filterChain)
////            throws ServletException, IOException {
////
////        String header = request.getHeader("Authorization");
////
////        if (header == null || !header.startsWith("Bearer ")) {
////            filterChain.doFilter(request, response);
////            return;
////        }
////
////        String token = header.substring(7);
////        System.err.println("Token in header : "+token);
////        String username = jwtUtil.extractUsername(token);
////
////        //checks if username is valid and authentication for current request is not set
////        if (username != null &&
////            SecurityContextHolder.getContext().getAuthentication() == null) {
////
////            UserDetails userDetails = service.loadUserByUsername(username);
////
////            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
////
////                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
////                        new UsernamePasswordAuthenticationToken(
////                                userDetails,
////                                null,
////                                userDetails.getAuthorities()
////                        );
////
////                //used to set details like ip address and sessionId(null in case of jwt)
////                usernamePasswordAuthenticationToken
////                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                
////                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
////                System.out.println(userDetails.getAuthorities());
////            }
////        }
////        else {
////        	System.err.println("Token is not validated");
////        }
////
////        filterChain.doFilter(request, response);
////    }
////}

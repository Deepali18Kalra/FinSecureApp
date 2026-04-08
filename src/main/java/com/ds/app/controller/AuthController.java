//package com.ds.app.controller;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ds.app.dto.LoginRequestDTO;
//import com.ds.app.dto.LoginResponseDTO;
//import com.ds.app.jwtutil.JWTUtil;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/finsecure/public")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JWTUtil jwtUtil;
//
//    // ✅ BASIC LOGIN API
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDTO> login(
//            @Valid @RequestBody LoginRequestDTO request) {
//
//        // ✅ Authenticate user
//        Authentication authentication =
//                authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(
//                                request.getUsername(),
//                                request.getPassword()
//                        )
//                );
//
//        // ✅ Extract roles (without ROLE_ prefix)
//        List<String> roles =
//                authentication.getAuthorities()
//                        .stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .map(role -> role.replace("ROLE_", ""))
//                        .collect(Collectors.toList());
//
//        // ✅ Generate JWT
//        String token =
//                jwtUtil.generateToken(request.getUsername(), roles);
//
//        // ✅ Build response
//        LoginResponseDTO response = new LoginResponseDTO();
//        response.setToken(token);
//        response.setUsername(request.getUsername());
//        response.setRoles(roles);
//
//        return ResponseEntity.ok(response);
//    }
//}
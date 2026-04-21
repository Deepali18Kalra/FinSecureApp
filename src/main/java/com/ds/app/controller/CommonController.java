package com.ds.app.controller;

import com.ds.app.dto.request.AppUserRequestBodyDTO;
import com.ds.app.dto.response.JWTResponseDTO;
import com.ds.app.dto.response.SignUpUserResponseDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.enums.UserRole;
import com.ds.app.jwtutil.JWTUtil;
import com.ds.app.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finsecure/public")
public class CommonController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<SignUpUserResponseDTO> registerUser(@RequestBody AppUser user) {
        AppUser savedUser = appUserService.registerAppUser(user);
        SignUpUserResponseDTO dto = new SignUpUserResponseDTO(savedUser.getUsername(), savedUser.getRole());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> loginUser(@RequestBody AppUserRequestBodyDTO appUserRequestBodyDto) throws Exception {
        String username = appUserRequestBodyDto.getUsername();
        String password = appUserRequestBodyDto.getPassword();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            System.out.println("Username password authenticated");
            System.err.println("user with username loaded");
            String token = jwtUtil.generateToken(username);

            UserRole role = appUserService.findByUsername(username)
                    .map(AppUser::getRole)
                    .orElseThrow(() -> new RuntimeException("Role not found for user: " + username));

            boolean isValid = token != null;

            JWTResponseDTO jwtResponseDto = new JWTResponseDTO(token, username, isValid, role);

            return new ResponseEntity<>(jwtResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }
    }
}
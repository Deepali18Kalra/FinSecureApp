package com.ds.app.dto;

import com.ds.app.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JWTResponseDTO {

    private String token;
    private String username;
    private Long userId;
    private String role;
    private boolean valid;

    public JWTResponseDTO(String token, String username, Long userId, String role, boolean valid) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.role = role;
        this.valid = valid;
    }
}

//public class JWTResponseDTO {
//
//	private String token;
//	private String username;
//	private boolean isValid;
//}
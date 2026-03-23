package com.ds.app.dto;

import com.ds.app.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpUserResponseDTO {

	private String username;
	private UserRole role;
}

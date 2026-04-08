package com.ds.app.dto.response;

import com.ds.app.enums.UserRole;
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

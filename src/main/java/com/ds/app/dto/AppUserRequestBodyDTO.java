package com.ds.app.dto;

import com.ds.app.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserRequestBodyDTO {

	private String username;
	private String password;
}

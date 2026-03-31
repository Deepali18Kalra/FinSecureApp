package com.ds.app.entity;

import com.ds.app.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;
	private String username;
	private String password;
	
	private Integer failedLoginAttemptsCount = 0;
	private Boolean isAccountLocked = false;
	
	@Enumerated(EnumType.STRING)
	@EqualsAndHashCode.Exclude
	private UserRole role;
}

package com.ds.app.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmployeeStatus;
import com.ds.app.enums.EmploymentType;
import com.ds.app.enums.Gender;
import com.ds.app.enums.SkillStatus;
import com.ds.app.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeProfileResponseDTO {

	private Long userId;
	private String username;
	private UserRole role;
	
	private String employeeCode;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private LocalDate dateOfBirth;
	private Gender gender;
	private String designation;
	private LocalDate joiningDate;
	private String addressLine;
	private String city;
	private String state;
	private String country;
	private String pincode;
	private EmploymentType employmentType;
	private String profilePhotoUrl;
	private Boolean isAccountLocked;
	private Boolean isDeleted;
	private Boolean isEscalated;
	
	private Boolean isProfileComplete;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	

}//end class

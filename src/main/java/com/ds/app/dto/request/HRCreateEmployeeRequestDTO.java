package com.ds.app.dto.request;

import java.time.LocalDate;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;
import com.ds.app.enums.Gender;
import com.ds.app.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HRCreateEmployeeRequestDTO {
	
	
	@NotBlank(message = "Username Is Required")
	@Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
	private String username;
	
	@NotBlank(message = "Password Is Required")
	@Size(min = 6, max = 100, message = "Password must be atleast 6 characters")
	private String password;


    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation cannot exceed 100 characters")
    private String designation;
    
    @NotNull(message = "Date of joining is required")
    @PastOrPresent(message = "Date of joining cannot be in the future")
    private LocalDate dateOfJoining;
    
    @NotNull(message = "Role is required")
    private UserRole role;   // HR can pass EMPLOYEE, HR, FINANCE, ADMIN

    private EmploymentType employmentType;

    private CertificationStatus certificationStatus;

    private String certificationName;

    private EmployeeExperience employeeExperience;

}

package com.ds.app.dto.request;

import java.time.LocalDate;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;
import com.ds.app.enums.UserRole;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeHRUpdateDTO {
 
    // ── Employee code ──────────────────────────────────────────────────
    // HR can reassign an employeeCode if needed.
    // Service checks for duplicates before saving.
    @Size(max = 20, message = "Employee code cannot exceed 20 characters")
    private String employeeCode;
    
    
    @NotNull(message = "Role is required")
    private UserRole role;   // HR can pass EMPLOYEE, HR, FINANCE, ADMIN
 
    @Size(max = 100, message = "Designation cannot exceed 100 characters")
    private String designation;
 
    private EmploymentType employmentType;
 
 
    // ── Joining date ───────────────────────────────────────────────────
    @PastOrPresent(message = "Joining date cannot be in the future")
    private LocalDate joiningDate;
 
  
 
    
}

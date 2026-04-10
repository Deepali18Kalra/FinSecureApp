package com.ds.app.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;
import com.ds.app.enums.Gender;
import com.ds.app.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDashboardDTO {
	
	  // ── Identity ───────────────────────────────────────────────────
    private Long userId;
    private String username;
    private String employeeCode;
    private UserRole role;

    // ── Personal info ──────────────────────────────────────────────
    private String firstName;
    private String lastName;
    private String fullName;        // firstName + lastName combined
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;

    // ── Address ────────────────────────────────────────────────────
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String fullAddress;     // complete address in one string

    // ── Professional info ──────────────────────────────────────────
    private String designation;
    private LocalDate joiningDate;
    private EmploymentType employmentType;
  
    // ── Work duration ──────────────────────────────────────────────
    private Long daysWorked;        // total days since joining
    private Long monthsWorked;      // total months since joining
    private Long yearsWorked;       // total years since joining

    // ── Photo ──────────────────────────────────────────────────────
    private String profilePhotoUrl;
    private Boolean hasPhoto;       // true if photo uploaded

    // ── Profile completion ─────────────────────────────────────────
    private Boolean isProfileComplete;      // true if all fields filled
    private List<String> missingFields; // list of null fields

    // ── Account status ─────────────────────────────────────────────
    private Boolean isAccountLocked;
    private Boolean isDeleted;
    private Boolean isEscalated;

    // ── Timestamps ─────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}

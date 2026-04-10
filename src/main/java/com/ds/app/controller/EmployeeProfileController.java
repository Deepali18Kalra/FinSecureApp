package com.ds.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ds.app.dto.request.EmployeeUpdateRequestDTO;
import com.ds.app.dto.response.EmployeeDashboardDTO;
import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.ProfilePhotoResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeCRUDService;
import com.ds.app.service.EmployeePhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Validated
@Tag(name = "Employee Profile", description = "Employee self-service — " + "view profile, update personal info, " + "upload photo and view dashboard")

public class EmployeeProfileController {

   
    private static final Logger logger = LoggerFactory.getLogger(EmployeeProfileController.class);

    @Autowired
    private EmployeeCRUDService employeeCRUDService;

    @Autowired
    private EmployeePhotoService employeePhotoService;
    
    @Autowired
    private EmployeeRepository employeeRepo;

 
    @GetMapping("/finsecure/employee/profile")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(
        summary = "View own profile (Employee)",
        description =
            "Employee views their own profile.\n\n" +
            "HR-assigned fields (employeeCode, department, " +
            "designation) are set.\n\n" +
            "Personal fields (name, email, phone, address) " +
            "are null until employee fills them.\n\n" +
            "No userId needed — resolved from JWT."
    )
    public ResponseEntity<EmployeeProfileResponseDTO> getOwnProfile(@AuthenticationPrincipal MyUserDetails userDetails) throws Exception {

        logger.info("GET own profile for: {}", userDetails.getUsername());

        return ResponseEntity.ok(employeeCRUDService.getOwnProfile(userDetails.getUsername()));
    }

 
    @PutMapping("/finsecure/employee/update/profile")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(
        summary = "Update personal information (Employee)",
        description =
            "Employee fills in personal info.\n\n" +
            "**Can update:** firstName, lastName, email, " +
            "phoneNumber, dateOfBirth, gender, " +
            "addressLine, city, state, country, pincode.\n\n" +
            "**Cannot update:** department, designation, " +
            "certificationStatus — HR controls these.\n\n" +
            "Partial update — only send fields to change."
    )
    public ResponseEntity<EmployeeProfileResponseDTO> updateOwnProfile(@Valid @RequestBody EmployeeUpdateRequestDTO dto,@AuthenticationPrincipal MyUserDetails userDetails) throws Exception {

        logger.info("Employee '{}' updating personal info",userDetails.getUsername());

        return ResponseEntity.ok(employeeCRUDService.updateOwnProfile(dto, userDetails.getUsername()));
    }

   
    @PostMapping(value = "/finsecure/employee/profile/photo",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(
        summary = "Upload profile photo (Employee)",
        description =
            "Accepted formats: JPG, JPEG, PNG.\n\n" +
            "Maximum size: 2MB.\n\n" +
            "Use multipart/form-data with field name 'file'.\n\n" +
            "Uploading again replaces the previous photo.\n\n" +
            "Same photo cannot be used by two employees."
    )
    public ResponseEntity<ProfilePhotoResponseDTO> uploadProfilePhoto(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal MyUserDetails userDetails) throws Exception {

        logger.info("Photo upload for: {}",userDetails.getUsername());

        return ResponseEntity.ok(employeePhotoService.uploadProfilePhoto(file, userDetails.getUsername()));
    }

   
    @GetMapping("/finsecure/employee/dashboard")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(
        summary = "Employee dashboard (Employee only)",
        description =
            "Returns complete summary of employee profile.\n\n" +
            "Includes work duration calculated from joining date.\n\n" +
            "Shows isProfileComplete and missingFields.\n\n" +
            "No userId needed — resolved from JWT."
    )
    public ResponseEntity<EmployeeDashboardDTO>
            getEmployeeDashboard(@AuthenticationPrincipal UserDetails userDetails) throws Exception {

        logger.info("Dashboard for: {}", userDetails.getUsername());

        return ResponseEntity.ok(employeeCRUDService.getEmployeeDashboard(userDetails.getUsername()));
    }
    
    private Long getUserId(String username) {
    	
        return employeeRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException( "Employee not found: " + username))
                .getUserId();
    }

}
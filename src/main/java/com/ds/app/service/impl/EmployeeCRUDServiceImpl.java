package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.*;
import com.ds.app.dto.response.*;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeDocument;
import com.ds.app.entity.EmployeeEducation;
import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.UserRole;
import com.ds.app.exception.DuplicateEmailException;
import com.ds.app.exception.DuplicatePhoneException;
import com.ds.app.exception.EmployeeCodeAlreadyExistsException;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.exception.ProfileDeletedException;
import com.ds.app.utils.MaskingUtil;
import com.ds.app.repository.EmployeeDocumentRepository;
import com.ds.app.repository.EmployeeEducationRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.AppUserRepository;
import com.ds.app.service.EmployeeCRUDService;

import jakarta.transaction.Transactional;

@Service
public class EmployeeCRUDServiceImpl implements EmployeeCRUDService {
	
	
	@Autowired
	EmployeeRepository employeeRepo;
	
	@Autowired
	AppUserRepository appUserRepository;
	
	@Autowired
	EmployeeEducationRepository iEducationRepository;
	
	@Autowired
	EmployeeDocumentRepository iemDocumentRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeCRUDServiceImpl.class);


		@Override
		@Transactional
		public HRCreateEmployeeResponseDTO createEmployeeByHR(HRCreateEmployeeRequestDTO dto, String hrUsername) throws Exception {
			
	
	        logger.info("HR '{}' creating user: {}", hrUsername, dto.getUsername());
	 
	        if (employeeRepo.existsByUsername(dto.getUsername()))
	            throw new RuntimeException("Username already exists: " + dto.getUsername());
	 
	        Long userId;
	        String employeeCode = null;
	 
	        if (dto.getRole() != UserRole.ADMIN && dto.getRole() != UserRole.SYSTEM) {
	
	            if (dto.getRole() == UserRole.EMPLOYEE) {
	                if (dto.getDesignation() == null || dto.getDesignation().isBlank())
	                    throw new RuntimeException("Designation required for EMPLOYEE");
	            }
	 
	            Employee employee = new Employee();
	            employee.setUsername(dto.getUsername());
	            employee.setPassword(passwordEncoder.encode(dto.getPassword()));
	            employee.setRole(dto.getRole());
	            employee.setFailedLoginAttemptsCount(0);
	            employee.setIsAccountLocked(false);
	            employee.setDesignation(dto.getDesignation());
	            employee.setEmploymentType(dto.getEmploymentType());
	            employee.setJoiningDate(dto.getDateOfJoining());
	            employee.setIsDeleted(false);
	            employee.setIsEscalated(false);
	 
	            Employee saved = employeeRepo.save(employee);
	            userId = saved.getUserId();
	 
	            employeeCode = "EMP" + String.format("%04d", userId);
	            if (employeeRepo.existsByEmployeeCode(employeeCode))
	                throw new EmployeeCodeAlreadyExistsException(employeeCode);
	            saved.setEmployeeCode(employeeCode);
	            employeeRepo.save(saved);
	 
	        } else {
	
	            AppUser appUser = new AppUser();
	            appUser.setUsername(dto.getUsername());
	            appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
	            appUser.setRole(dto.getRole());
	            appUser.setFailedLoginAttemptsCount(0);
	            appUser.setIsAccountLocked(false);
	            AppUser saved = appUserRepository.save(appUser);
	            userId = saved.getUserId();
	        }
	 
	        logger.info("User created. userId={}, role={}", userId, dto.getRole());
	 
	        return HRCreateEmployeeResponseDTO.builder()
	                .userId(userId)
	                .username(dto.getUsername())
	                .temporaryPassword(dto.getPassword())
	                .employeeCode(employeeCode)
	                .userRole(dto.getRole())
	                .employmentType(dto.getEmploymentType())
	                .message("Account created. Share credentials with the user.")
	                .build();
		}
	
		@Override
		public EmployeeProfileResponseDTO getOwnProfile(String username) throws Exception {
			
			logger.info("Fetching own profile for username: {}", username);
	        Employee employee = employeeRepo.findByUsername(username)
	                .orElseThrow(() -> new EmployeeNotFoundException1("Employee not found: " + username));
	        return mapToResponseDTO(employee);
	    
		}
	
		@Override
		public EmployeeProfileResponseDTO updateOwnProfile(EmployeeUpdateRequestDTO dto, String username) throws Exception {
			 logger.info("Employee '{}' updating own profile", username);
		        Employee employee = findActiveByUsername(username);
		 
		        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
		            if (employee.getEmail() == null || !dto.getEmail().equals(employee.getEmail()))
		                if (employeeRepo.existsByEmailAndUserIdNot(dto.getEmail(),employee.getUserId()))
		                    throw new DuplicateEmailException(dto.getEmail());
		            employee.setEmail(dto.getEmail());
		        }
		 
		        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
		        	
		            if (employee.getPhoneNumber() == null || !dto.getPhoneNumber().equals(employee.getPhoneNumber()))
		            	
		                if (employeeRepo.existsByPhoneNumberAndUserIdNot(dto.getPhoneNumber(), employee.getUserId()))
		                	
		                    throw new DuplicatePhoneException(dto.getPhoneNumber());
		            employee.setPhoneNumber(dto.getPhoneNumber());
		        }
		 
		        if (dto.getFirstName()   != null && !dto.getFirstName().isBlank())
		            employee.setFirstName(dto.getFirstName());
		        if (dto.getLastName()    != null && !dto.getLastName().isBlank())
		            employee.setLastName(dto.getLastName());
		        if (dto.getDateOfBirth() != null)
		            employee.setDateOfBirth(dto.getDateOfBirth());
		        if (dto.getGender()      != null)
		            employee.setGender(dto.getGender());
		        if (dto.getAddressLine() != null && !dto.getAddressLine().isBlank())
		            employee.setAddressLine(dto.getAddressLine());
		        if (dto.getCity()        != null && !dto.getCity().isBlank())
		            employee.setCity(dto.getCity());
		        if (dto.getState()       != null && !dto.getState().isBlank())
		            employee.setState(dto.getState());
		        if (dto.getCountry()     != null && !dto.getCountry().isBlank())
		            employee.setCountry(dto.getCountry());
		        if (dto.getPincode()     != null && !dto.getPincode().isBlank())
		            employee.setPincode(dto.getPincode());
		 
		        Employee saved = employeeRepo.save(employee);
		        logger.info("Profile updated for userId: {}", saved.getUserId());
		        return mapToResponseDTO(saved);
		}
	
		@Override
		public EmployeeProfileResponseDTO getEmployeeById(Long userId) throws Exception {
			
			 logger.info("HR fetching employee for userId: {}", userId);
		        Employee employee = employeeRepo.findByUserIdAndIsDeletedFalse(userId)
		                .orElseThrow(() -> new EmployeeNotFoundException1(userId));
		        
		        return mapToMaskedResponseDTO(employee);
		}
	
		@Override
		public EmployeeProfileResponseDTO updateEmployeeByHr(Long userId, EmployeeHRUpdateDTO dto, String hrUsername) throws Exception {
			
			   logger.info("HR '{}' updating userId: {}", hrUsername, userId);
			   
		        Employee employee = employeeRepo.findByUserIdAndIsDeletedFalse(userId)
		                .orElseThrow(() -> new EmployeeNotFoundException1(userId));
		 
		        applyHrUpdates(employee, dto);
		 
		        Employee saved = employeeRepo.save(employee);
		        logger.info("HR updated userId: {}", saved.getUserId());
		        return mapToMaskedResponseDTO(saved);
		}
		
		
		  private Employee findActiveByUsername(String username) throws Exception {
		        Employee employee = employeeRepo.findByUsername(username)
		                .orElseThrow(() -> new EmployeeNotFoundException1("Employee not found: " + username));
		        
		        if (Boolean.TRUE.equals(employee.getIsDeleted()))
		            throw new ProfileDeletedException(employee.getUserId());
		        
		        return employee;
		    }
		 
		    // Full data — employee sees own profile unmasked
		    private EmployeeProfileResponseDTO mapToResponseDTO(Employee e) {
		        EmployeeProfileResponseDTO dto = new EmployeeProfileResponseDTO();
		        dto.setUserId(e.getUserId());
		        dto.setUsername(e.getUsername());
		        dto.setRole(e.getRole());
		        dto.setEmployeeCode(e.getEmployeeCode());
		        dto.setFirstName(e.getFirstName());
		        dto.setLastName(e.getLastName());
		        dto.setEmail(e.getEmail());               // unmasked
		        dto.setPhoneNumber(e.getPhoneNumber());   // unmasked
		        dto.setDateOfBirth(e.getDateOfBirth());
		        dto.setGender(e.getGender());
		        dto.setDesignation(e.getDesignation());
		        dto.setJoiningDate(e.getJoiningDate());
		        dto.setEmploymentType(e.getEmploymentType());
		        dto.setAddressLine(e.getAddressLine());
		        dto.setCity(e.getCity());
		        dto.setState(e.getState());
		        dto.setCountry(e.getCountry());
		        dto.setPincode(e.getPincode());
		        dto.setIsDeleted(e.getIsDeleted());
		        dto.setIsAccountLocked(e.getIsAccountLocked());
		        dto.setIsEscalated(e.getIsEscalated());
		        dto.setProfilePhotoUrl(e.getProfilePhotoUrl());
		        dto.setCreatedAt(e.getCreatedAt());
		        dto.setUpdatedAt(e.getUpdatedAt());
		        dto.setIsProfileComplete(isProfileComplete(e));
		        
		        return dto;
		    }
		 
		    // Masked — HR sees masked email and phone
		    private EmployeeProfileResponseDTO mapToMaskedResponseDTO(Employee e) {
		    	
		        EmployeeProfileResponseDTO dto = mapToResponseDTO(e);
		        dto.setEmail(MaskingUtil.maskEmail(e.getEmail()));
		        dto.setPhoneNumber(MaskingUtil.maskPhone(e.getPhoneNumber()));
		        return dto;
		        
		    }
		 
		    private void applyHrUpdates(Employee e, EmployeeHRUpdateDTO dto) throws EmployeeCodeAlreadyExistsException {
		    	
		        if (dto.getEmployeeCode() != null && !dto.getEmployeeCode().isBlank()) {
		            if (employeeRepo.existsByEmployeeCodeAndUserIdNot(dto.getEmployeeCode(), e.getUserId()))
		                throw new EmployeeCodeAlreadyExistsException(dto.getEmployeeCode());
		            e.setEmployeeCode(dto.getEmployeeCode());
		        }
		        if (dto.getDesignation()  != null && !dto.getDesignation().isBlank())
		            e.setDesignation(dto.getDesignation());
		        if(dto.getRole() !=null )
		        	e.setRole(dto.getRole());
		        if (dto.getEmploymentType()     != null) e.setEmploymentType(dto.getEmploymentType());
		    }
		    private Boolean isProfileComplete(Employee e) {
		        return e.getFirstName()   != null && !e.getFirstName().isBlank()
		            && e.getLastName()    != null && !e.getLastName().isBlank()
		            && e.getEmail()       != null && !e.getEmail().isBlank()
		            && e.getPhoneNumber() != null && !e.getPhoneNumber().isBlank()
		            && e.getDateOfBirth() != null
		            && e.getGender()      != null
		            && e.getAddressLine() != null && !e.getAddressLine().isBlank()
		            && e.getCity()        != null && !e.getCity().isBlank()
		            && e.getState()       != null && !e.getState().isBlank()
		            && e.getCountry()     != null && !e.getCountry().isBlank()
		            && e.getPincode()     != null && !e.getPincode().isBlank()
		        	&& e.getProfilePhotoUrl() != null;
		    }
	
			@Override
			@Transactional
			public EmployeeDashboardDTO getEmployeeDashboard(String username) throws Exception {
				
				    logger.info("Fetching dashboard for: {}", username);
	
				    Employee e = employeeRepo.findByUsername(username)
				            .orElseThrow(() -> new EmployeeNotFoundException1("Employee not found: " + username));
	
				    if (Boolean.TRUE.equals(e.getIsDeleted()))
				        throw new ProfileDeletedException(e.getUserId());
	
				    String fullName = null;
				    if (e.getFirstName() != null && e.getLastName() != null)
				        fullName = e.getFirstName() + " " + e.getLastName();
				    else if (e.getFirstName() != null)
				        fullName = e.getFirstName();
	
				    String fullAddress = null;
				    if (e.getAddressLine() != null && e.getCity() != null) {
				        fullAddress = e.getAddressLine() + ", " +
				                      e.getCity() + ", " +
				                      e.getState() + " - " +
				                      e.getPincode() + ", " +
				                      e.getCountry();
				    }
	
				    Long daysWorked   = null;
				    Long monthsWorked = null;
				    Long yearsWorked  = null;
				    if (e.getJoiningDate() != null) {
				        daysWorked   = java.time.temporal.ChronoUnit.DAYS
				                .between(e.getJoiningDate(), LocalDate.now());
				        monthsWorked = daysWorked / 30;
				        yearsWorked  = daysWorked / 365;
				    }
				    
				    java.util.List<String> missingFields = new java.util.ArrayList<>();
				    if (e.getFirstName()   == null) missingFields.add("firstName");
				    if (e.getLastName()    == null) missingFields.add("lastName");
				    if (e.getEmail()       == null) missingFields.add("email");
				    if (e.getPhoneNumber() == null) missingFields.add("phoneNumber");
				    if (e.getDateOfBirth() == null) missingFields.add("dateOfBirth");
				    if (e.getGender()      == null) missingFields.add("gender");
				    if (e.getAddressLine() == null) missingFields.add("addressLine");
				    if (e.getCity()        == null) missingFields.add("city");
				    if (e.getState()       == null) missingFields.add("state");
				    if (e.getCountry()     == null) missingFields.add("country");
				    if (e.getPincode()     == null) missingFields.add("pincode");
				    if (e.getProfilePhotoUrl() == null) missingFields.add("profilePhotoUrl");
	
	
				    boolean isProfileComplete = missingFields.isEmpty();
	
				    logger.info("Dashboard for userId: {} — complete: {}", e.getUserId(), isProfileComplete);
	
				    return EmployeeDashboardDTO.builder()
				            
				            .userId(e.getUserId())
				            .username(e.getUsername())
				            .employeeCode(e.getEmployeeCode())
				            .role(e.getRole())
				            .firstName(e.getFirstName())
				            .lastName(e.getLastName())
				            .fullName(fullName)
				            .email(e.getEmail())
				            .phoneNumber(e.getPhoneNumber())
				            .dateOfBirth(e.getDateOfBirth())
				            .gender(e.getGender())
				            .addressLine(e.getAddressLine())
				            .city(e.getCity())
				            .state(e.getState())
				            .country(e.getCountry())
				            .pincode(e.getPincode())
				            .fullAddress(fullAddress)
				            .designation(e.getDesignation())
				            .joiningDate(e.getJoiningDate())
				            .employmentType(e.getEmploymentType())
				            .daysWorked(daysWorked)
				            .monthsWorked(monthsWorked)
				            .yearsWorked(yearsWorked)
				            .profilePhotoUrl(e.getProfilePhotoUrl())
				            .hasPhoto(e.getProfilePhotoUrl() != null)
				            .isProfileComplete(isProfileComplete)
				            .missingFields(missingFields)
				            .isAccountLocked(e.getIsAccountLocked())
				            .isDeleted(e.getIsDeleted())
				            .isEscalated(e.getIsEscalated())
				            .createdAt(e.getCreatedAt())
				            .updatedAt(e.getUpdatedAt())
				            .build();
				}
			
			
			@Override
			@Transactional
			public EmployeeExportDTO exportEmployeeProfile(Long userId) throws Exception {
				
			    logger.info("Exporting profile for userId: {}", userId);
			    Employee employee = employeeRepo
			            .findByUserIdAndIsDeletedFalse(userId)
			            .orElseThrow(() -> new EmployeeNotFoundException1(userId));
			    EmployeeProfileResponseDTO profile = mapToMaskedResponseDTO(employee);
			    List<EmployeeEducationResponseDTO> education = iEducationRepository
			            .findByEmployeeUserIdOrderByPassingYearDesc(userId)
			            .stream()
			            .map(this::mapEducationToResponse)
			            .toList();
	
			    List<EmployeeDocumentResponseDTO> documents = iemDocumentRepository
			            .findByEmployeeUserId(userId)
			            .stream()
			            .map(d -> mapDocumentToResponse(d, true)) 
			            .toList();
			 
			    logger.info("Export complete for userId: {} — " + "education: {}, documents: {}", userId, education.size(), documents.size());
			 
			    return EmployeeExportDTO.builder()
			            .profile(profile)
			            .education(education)
			            .documents(documents)
			            .build();
			}
			
			private EmployeeEducationResponseDTO mapEducationToResponse(EmployeeEducation e) {
				
			    return EmployeeEducationResponseDTO.builder()
			            .eduId(e.getEduId())
			            .userId(e.getEmployee().getUserId())
			            .degree(e.getDegree())
			            .institution(e.getInstitution())
			            .fieldOfStudy(e.getFieldOfStudy())
			            .passingYear(e.getPassingYear())
			            .percentage(e.getPercentage())
			            .grade(e.getGrade())
			            .location(e.getLocation())
			            .createdAt(e.getCreatedAt())
			            .updatedAt(e.getUpdatedAt())
			            .build();
			}
			 
		
			private EmployeeDocumentResponseDTO mapDocumentToResponse( EmployeeDocument d, boolean masked) {
				
			    return EmployeeDocumentResponseDTO.builder()
			            .documentId(d.getDocumentId())
			            .userId(d.getEmployee().getUserId())
			            .documentType(d.getDocumentType())
			            .documentNumber(
			                    masked && d.getDocumentNumber() != null
			                    ? MaskingUtil.maskLast(4,
			                            d.getDocumentNumber())
			                    : d.getDocumentNumber())
			            .documentUrl(d.getDocumentUrl())
			            .expiryDate(d.getExpiryDate())
			            .isVerified(d.getIsVerified())
			            .createdAt(d.getCreatedAt())
			            .updatedAt(d.getUpdatedAt())
			            .build();
			}


}//end class

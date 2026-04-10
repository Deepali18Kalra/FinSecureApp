package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ds.app.dto.request.HRCreateEmployeeRequestDTO;
import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.HRCreateEmployeeResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EmploymentType;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeCRUDServiceImplTest {
	
	@Mock
	private EmployeeRepository iEmployeeRepo;
	
	@Mock
	private AppUserRepository appUserRepo;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private EmployeeCRUDServiceImpl employeeCRUDService;
	
	// create employee bY hR
	@Test
	@Description("create employee by HR method test")
	void testCreateEmployeeByHR() throws Exception {
		
		//Input of actual method call
		HRCreateEmployeeRequestDTO dto = new HRCreateEmployeeRequestDTO();
		
		dto.setUsername("john_doe");
		dto.setPassword("emp1234");
		dto.setRole(UserRole.EMPLOYEE);
		dto.setDesignation("Developer");
		dto.setEmploymentType(EmploymentType.FULL_TIME);
		dto.setEmployeeExperience(EmployeeExperience.FRESHER);
		dto.setCertificationStatus(CertificationStatus.NON_CERTIFIED);
		dto.setDateOfJoining(LocalDate.of(2024, 01, 03));
		
		//expected ouput from repo
		
		Employee saved = new Employee();
		saved.setUserId(13L);
		saved.setUsername("john_doe");
		saved.setPassword("hashedPassword");
		saved.setRole(UserRole.EMPLOYEE);
		saved.setDesignation("Developer");
		saved.setEmploymentType(EmploymentType.FULL_TIME);
		saved.setEmployeeExperience(EmployeeExperience.FRESHER);
		saved.setCertificationStatus(CertificationStatus.NON_CERTIFIED);
		saved.setIsDeleted(false);
		saved.setIsEscalated(false);
		saved.setIsAccountLocked(false);
		saved.setFailedLoginAttemptsCount(0);
		
		//expected output from repo-second save with employeecode
		Employee savedWithCode = new Employee();
		savedWithCode.setUserId(13L);
		savedWithCode.setUsername("john_doe");
		savedWithCode.setEmployeeCode("EMP0013");
		savedWithCode.setRole(UserRole.EMPLOYEE);
		
		//code to configure save operation through mockito
		//expected output from method
		when(iEmployeeRepo.existsByUsername("john_doe")).thenReturn(false);
		when(passwordEncoder.encode("emp1234")).thenReturn("hashedPassword");
		when(iEmployeeRepo.save(any(Employee.class))).thenReturn(saved).thenReturn(savedWithCode);
		when(iEmployeeRepo.existsByEmployeeCode("EMP0013")).thenReturn(false);
		
		//call actual method
		HRCreateEmployeeResponseDTO result = employeeCRUDService.createEmployeeByHR(dto, "hr_user");
		
		//assert
		assertNotNull(result);
		assertEquals("john_doe", result.getUsername());
		assertEquals("EMP0013", result.getEmployeeCode());
		assertEquals(UserRole.EMPLOYEE, result.getUserRole());
		
		verify(iEmployeeRepo,times(2)).save(any(Employee.class));
	
	}

	@Test
	@Description("get own profile method test")   
	void testGetOwnProfile() throws Exception {
		

		        // Sample Input for actual method call
		        String username = "john_doe";

		        // Expected Output from Repo
		        Employee savedEmployee = new Employee();
		        savedEmployee.setUserId(13L);
		        savedEmployee.setUsername("john_doe");
		        savedEmployee.setRole(UserRole.EMPLOYEE);
		        savedEmployee.setEmployeeCode("EMP0013");
		        savedEmployee.setFirstName("John");
		        savedEmployee.setLastName("Doe");
		        savedEmployee.setEmail("john@gmail.com");
		        savedEmployee.setPhoneNumber("9876543210");
		        savedEmployee.setDesignation("Developer");
		        savedEmployee.setIsDeleted(false);
		        savedEmployee.setIsAccountLocked(false);
		        savedEmployee.setIsEscalated(false);

		        // Code to configure save operation through mockito
		        // and Expected output from method
		        when(iEmployeeRepo.findByUsername("john_doe")).thenReturn(Optional.of(savedEmployee));

		        // Call actual method
		        EmployeeProfileResponseDTO result = employeeCRUDService.getOwnProfile(username);

		        // Assert
		        assertNotNull(result);
		        assertEquals(13L, result.getUserId());
		        assertEquals("john_doe", result.getUsername());
		        assertEquals("John", result.getFirstName());
		        assertEquals("Doe", result.getLastName());
		        assertEquals("john@gmail.com", result.getEmail());
		        verify(iEmployeeRepo, times(1)).findByUsername("john_doe");

	}

	@Test
	@Description("get employee by id method test")
	void testGetEmployeeById() throws Exception {
		

		    // Sample Input for actual method call
		    Long userId = 13L;

		    // Expected Output from Repo
		    Employee savedEmployee = new Employee();
		    savedEmployee.setUserId(13L);
		    savedEmployee.setUsername("john_doe");
		    savedEmployee.setRole(UserRole.EMPLOYEE);
		    savedEmployee.setEmployeeCode("EMP0013");
		    savedEmployee.setFirstName("John");
		    savedEmployee.setLastName("Doe");
		    savedEmployee.setEmail("john@gmail.com");        // ← real email in entity
		    savedEmployee.setPhoneNumber("9876543210");      // ← real phone in entity
		    savedEmployee.setIsDeleted(false);
		    savedEmployee.setIsAccountLocked(false);
		    savedEmployee.setIsEscalated(false);

		    // Code to configure through mockito
		    // and Expected output from method
		    when(iEmployeeRepo.findByUserIdAndIsDeletedFalse(13L)).thenReturn(Optional.of(savedEmployee));

		    // Call actual method
		    EmployeeProfileResponseDTO result = employeeCRUDService.getEmployeeById(userId);

		    // Assert
		    assertNotNull(result);
		    assertEquals(Long.valueOf(13L), result.getUserId()); // ← Long fix
		    assertEquals("EMP0013", result.getEmployeeCode());
		    assertEquals("joh***@gmail.com", result.getEmail()); // ← masked
		    assertEquals("******3210", result.getPhoneNumber()); // ← masked
		    verify(iEmployeeRepo, times(1)).findByUserIdAndIsDeletedFalse(13L);
		}

		
	
	
	
    @Test
    @Description("get own profile throws exception when not found test")
    void testGetOwnProfile_NotFound() {

        // Sample Input for actual method call
        String username = "unknown_user";

        // Code to configure save operation through mockito
        when(iEmployeeRepo.findByUsername("unknown_user")).thenReturn(Optional.empty());

        // Call actual method and Assert exception
        assertThrows(Exception.class,
                () -> employeeCRUDService.getOwnProfile(username));
    }


    @Test
    @Description("create employee throws exception when username exists test")
    void testCreateEmployeeByHR_UsernameExists() {

        // Sample Input for actual method call
        HRCreateEmployeeRequestDTO dto = new HRCreateEmployeeRequestDTO();
        dto.setUsername("john_doe");
        dto.setPassword("emp1234");
        dto.setRole(UserRole.EMPLOYEE);

        // Code to configure save operation through mockito
        when(iEmployeeRepo.existsByUsername("john_doe")).thenReturn(true);

        // Call actual method and Assert exception
        assertThrows(RuntimeException.class,
                () -> employeeCRUDService.createEmployeeByHR(dto, "hr_user"));

        // Verify save was never called
        verify(iEmployeeRepo, never()).save(any(Employee.class));
    }

}//end class

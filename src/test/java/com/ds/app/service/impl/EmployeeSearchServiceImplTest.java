package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.EmployeeSimpleResponseDTO;
import com.ds.app.dto.response.EmployeeIncompleteResponseDTO;
import com.ds.app.dto.response.MonthlyStatDTO;
import com.ds.app.dto.response.PagedResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeSearchServiceImplTest {
	
	@Mock
	private EmployeeRepository iEmployeeRepo;
	
	@Mock
	private AppUserRepository appUserRepo;
	
	
	@InjectMocks
	private EmployeeSearchServiceImpl employeeSearchService;
	
	private Employee mockEmployee;
	
	private Pageable pageable;
	
	 @BeforeEach
	 void setUp() {
	        pageable = PageRequest.of(0, 10);  // ← initialize here

	        mockEmployee = new Employee();
	        mockEmployee.setUserId(13L);
	        mockEmployee.setUsername("john_doe");
	        mockEmployee.setRole(UserRole.EMPLOYEE);
	        mockEmployee.setIsDeleted(false);
	        mockEmployee.setIsAccountLocked(false);
	        mockEmployee.setIsEscalated(false);
	    }
	
	
		// TEST: getMonthlyStats
		@Test
		@Description("get monthly stats method test")
		void testGetMonthlyStats() {
	
		    // Sample Input
		    int year = 2024;
	
		    // Expected Output from Repo
		    List<Object[]> mockResults = new ArrayList<>();
		    Object[] row1 = {1, 3L};
		    Object[] row2 = {2, 5L};
		    mockResults.add(row1);
		    mockResults.add(row2);
	
		    // Code to configure through mockito
		    when(iEmployeeRepo.countByMonthAndYear(2024)).thenReturn(mockResults);
	
		    // Call actual method
		    List<MonthlyStatDTO> result = employeeSearchService.getMonthlyStats(year);
	
		    // Assert
		    assertNotNull(result);
		    assertEquals(12, result.size()); // always 12 months
		    assertEquals("January", result.get(0).getMonthName());
		    assertEquals(3L, result.get(0).getCount());
		}
	
		// TEST: getRecentlyJoined
		@Test
		@Description("get recently joined method test")
		void testGetRecentlyJoined() {
	
		    // Sample Input
		    int days = 30;
	
		    // Expected Output from Repo
		    Employee savedEmployee = new Employee();
		    savedEmployee.setUserId(13L);
		    savedEmployee.setUsername("john_doe");
		    savedEmployee.setRole(UserRole.EMPLOYEE);
		    savedEmployee.setIsDeleted(false);
		    savedEmployee.setIsAccountLocked(false);
		    savedEmployee.setIsEscalated(false);
	
		    Page<Employee> mockPage = new PageImpl<>(List.of(savedEmployee), pageable, 1);
	
		    // Code to configure through mockito
		    when(iEmployeeRepo.findRecentlyJoined(any(LocalDate.class),any(Pageable.class))).thenReturn(mockPage);
	
		    // Call actual method
		    PagedResponseDTO<EmployeeProfileResponseDTO> result = employeeSearchService.getRecentlyJoined(days, pageable);
	
		    // Assert
		    assertNotNull(result);
		    assertEquals(1, result.getData().size());
		}
	
		// TEST: getEmployeesWithoutPhoto
		@Test
		@Description("get employees without photo method test")
		void testGetEmployeesWithoutPhoto() {
	
		    // Expected Output from Repo
		    Employee savedEmployee = new Employee();
		    savedEmployee.setUserId(13L);
		    savedEmployee.setUsername("john_doe");
		    savedEmployee.setRole(UserRole.EMPLOYEE);
		    savedEmployee.setProfilePhotoUrl(null); // no photo
		    savedEmployee.setIsDeleted(false);
		    savedEmployee.setIsAccountLocked(false);
		    savedEmployee.setIsEscalated(false);
	
		    Page<Employee> mockPage = new PageImpl<>(List.of(savedEmployee), pageable, 1);
	
		    // Code to configure through mockito
		    when(iEmployeeRepo.findEmployeesWithoutPhoto(any(Pageable.class))).thenReturn(mockPage);
	
		    // Call actual method
		    PagedResponseDTO<EmployeeSimpleResponseDTO> result = employeeSearchService.getEmployeesWithoutPhoto(pageable);
	
		    // Assert
		    assertNotNull(result);
		    assertEquals(1, result.getData().size());
		}
	

}//end class

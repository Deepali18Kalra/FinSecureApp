package com.ds.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import com.ds.app.entity.Employee;
import com.ds.app.enums.UserRole;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeAdminServiceImplTest {
	
	
	@Mock
	private EmployeeRepository iEmployeeRepo;
	
	@InjectMocks
	private EmployeeAdminServiceImpl adminService;
	
	
	// soft delete employee

	@Test
	@Description("soft delete employee method test")
	void testSoftDeleteEmployee() throws Exception {
		
		// sample unit for method call
		
		Long userId = 13L;
		
		//expected output from repo
		Employee saved = new Employee();
		saved.setUserId(13L);
		saved.setUsername("john_doe");
		saved.setRole(UserRole.EMPLOYEE);
		saved.setIsDeleted(false);
		saved.setIsAccountLocked(false);
		
		//code to configure through mockito and expected output from method
		when(iEmployeeRepo.findByUserId(13L)).thenReturn(Optional.of(saved));
		when(iEmployeeRepo.save(any(Employee.class))).thenReturn(saved);
		
		//call actual method
		adminService.softDeleteEmployee(userId);
		
		//assert
		assertTrue(saved.getIsDeleted());
		verify(iEmployeeRepo,times(1)).save(any(Employee.class));
		
	}
	
	
	//restore employee

	@Test
	@Description("restore delted employee method test")
	void testRestoreEmployee() throws EmployeeNotFoundException1 {
		
		//sample uinput for actual method call

		Long userId = 13L;
		
		//expected output from repo
		Employee saved = new Employee();
		saved.setUserId(13L);
		saved.setUsername("john_doe");
		saved.setRole(UserRole.EMPLOYEE);
		saved.setIsDeleted(true);
		
		//code to configure through mockito and expected output from method
		when(iEmployeeRepo.findByUserId(13L)).thenReturn(Optional.of(saved));
		when(iEmployeeRepo.save(any(Employee.class))).thenReturn(saved);
		
		//call actual method
		adminService.restoreEmployee(userId);
				
		//assert
		assertFalse(saved.getIsDeleted());
		verify(iEmployeeRepo,times(1)).save(any(Employee.class));	
	}
	
	//soft delete employee-not found
	@Test
	@Description("soft delete throws exception when not found test")
	void testSoftDeleteEmployee_NotFound() {
		
	// sample input for actual method
	
		Long userId = 99L;
		
		when(iEmployeeRepo.findByUserId(99L)).thenReturn(Optional.empty());
		
		assertThrows(Exception.class, 
					() -> adminService.softDeleteEmployee(userId));
		
		verify(iEmployeeRepo,never()).save(any(Employee.class));
		
	}
	
	// restore employee -  not deleted
	@Test
	@Description("restore throws exception when not deleted test")
	void testRestoreEmployee_NotDeleted() {
		
		Long userId = 13L;
		
		Employee saved = new Employee();
		saved.setUserId(13L);
		saved.setIsDeleted(false);
		
		when(iEmployeeRepo.findByUserId(13L)).thenReturn(Optional.of(saved));
		

		assertThrows(Exception.class, 
					() -> adminService.restoreEmployee(userId));	
	}
	
	//soft delete employee - already deleted
	@Test
	@Description("soft delete does nothing when already deleted test")
	void testSoftDeleteEmployee_AlreadyDeleted() throws Exception {
		
		Long userId = 13L;
		
		Employee saved = new Employee();
		saved.setUserId(13L);
		saved.setIsDeleted(true);
		
		when(iEmployeeRepo.findByUserId(13L)).thenReturn(Optional.of(saved));

		 // Call actual method
		adminService.softDeleteEmployee(userId);
		
		 // Assert — save should NOT be called
		verify(iEmployeeRepo, never()).save(any(Employee.class));

	}
	

}//end class

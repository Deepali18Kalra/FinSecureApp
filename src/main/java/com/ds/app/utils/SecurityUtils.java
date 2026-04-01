package com.ds.app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ds.app.entity.Employee;
import com.ds.app.exception.CustomException;
import com.ds.app.repository.EmployeeRepository;

@Component
public class SecurityUtils {
	
	@Autowired
	private EmployeeRepository employeeRepo;

	public  Employee getLoggedInEmployee() {
		
		// Get UserDetails from SecurityContext
		// Auth teams filter already set this
		
		Object principal = SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
		
		//safety check
		if(!(principal instanceof UserDetails)) {
			throw new CustomException("User not authenticated as Employee");
		}
		
		UserDetails userDetails = (UserDetails) principal;
		
		//getUsername() return whatever Auth team
		//stored in their JWT setSubject()
		String username = userDetails.getUsername();
		
	
		//find and return full Employee object
		return employeeRepo.findByUsername(username)
				.orElseThrow(() -> new CustomException("Employee not found with username: " + username));
	}
	
	//helper - get just the username
	public String getLoggedInUsername() {
		Object principal = SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
		
		if(principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		
		
		
		return null;
	}
}
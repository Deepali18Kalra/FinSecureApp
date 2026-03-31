package com.ds.app.utils;

import com.ds.app.entity.Employee;
import com.ds.app.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
	
	private final IEmployeeRepository employeeRepo;

	public Employee getLoggedInEmployee() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (!(principal instanceof UserDetails)) {
			throw new RuntimeException("User not authenticated as Employee");
		}
		UserDetails userDetails = (UserDetails) principal;
		String username = userDetails.getUsername();

		return employeeRepo.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Employee not found with username: " + username));
	}

	public String getLoggedInUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}

		return null;
	}
}
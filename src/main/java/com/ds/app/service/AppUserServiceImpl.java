package com.ds.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.AppUserRepository;

import jakarta.transaction.Transactional;

@Service
public class AppUserServiceImpl implements AppUserService{

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	 @Autowired
	 private EmployeeRepository iEmployeeRepo;

	 
	 @Override
	 @Transactional
	 public AppUser registerAppUser(AppUser user) {

	    
	     if (appUserRepository.existsByUsername(user.getUsername())) {
	         throw new RuntimeException("Username already exists");
	     }

	    
	     String encodedPassword = passwordEncoder.encode(user.getPassword());

	     if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SYSTEM) {

	        
	         Employee employee = new Employee();

	         employee.setUsername(user.getUsername());
	         employee.setPassword(encodedPassword);  
	         employee.setRole(user.getRole());
	         employee.setFailedLoginAttemptsCount(0);
	         employee.setIsAccountLocked(false);

	         employee.setIsDeleted(false);
	         employee.setIsEscalated(false);

	         Employee saved = iEmployeeRepo.save(employee);
	         String employeeCode = "EMP" + String.format("%04d", saved.getUserId());
	         saved.setEmployeeCode(employeeCode);
	         iEmployeeRepo.save(saved);

	         return saved;

	     } else {
	         user.setPassword(encodedPassword);
	         user.setFailedLoginAttemptsCount(0);
	         user.setIsAccountLocked(false);
	         return appUserRepository.save(user);
	     }
	 }

	@Override
	public AppUser findByUsername(String username) {
		 return appUserRepository.findByUsername(username)
		            .orElseThrow(() -> new RuntimeException("User not found: " + username));
	}
}

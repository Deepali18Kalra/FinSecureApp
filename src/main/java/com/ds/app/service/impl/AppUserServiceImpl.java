package com.ds.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AppUserService;

@Service
public class AppUserServiceImpl implements AppUserService{

@Autowired
private iAppUserRepository appUserRepository;

@Autowired
private PasswordEncoder passwordEncoder;

 @Override
 public AppUser registerAppUser(AppUser user) {
 user.setPassword(passwordEncoder.encode(user.getPassword()));
 AppUser savedUser = appUserRepository.save(user);
 return savedUser;
 }

//public AppUser registerAppUser(AppUser user) {
//   user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//   if (user.getRole() == UserRole.EMPLOYEE) {
//       // If the incoming user is already an Employee (sent as subtype), cast and save directly
//       if (user instanceof Employee) {
//           Employee emp = (Employee) user;
//           // Ensure required fields have defaults if not provided
//           if (emp.getFirstName() == null || emp.getFirstName().isBlank()) {
//               emp.setFirstName(user.getUsername());
//           }
//           if (emp.getLastName() == null || emp.getLastName().isBlank()) {
//               emp.setLastName("-");
//           }
//           if (emp.getCurrentSalary() == null) {
//               emp.setCurrentSalary(0.0);
//           }
//           if (emp.getEmployeeCode() == null || emp.getEmployeeCode().isBlank()) {
//               emp.setEmployeeCode("EMP-" + user.getUsername().toUpperCase());
//           }
//           return appUserRepository.save(emp);
//       }
//
//       // Plain AppUser with EMPLOYEE role — wrap into Employee entity
//       Employee emp = new Employee();
//       emp.setUsername(user.getUsername());
//       emp.setPassword(user.getPassword());
//       emp.setRole(user.getRole());
//       emp.setFailedLoginAttemptsCount(0);
//       emp.setIsAccountLocked(false);
//       // Temporary defaults — HR module will update these
//       emp.setFirstName(user.getUsername());
//       emp.setLastName("-");
//       emp.setCurrentSalary(0.0);
//       emp.setIsActive(true);
//       emp.setEmployeeCode("EMP-" + user.getUsername().toUpperCase());
//       return appUserRepository.save(emp);
//   }
//
//   return appUserRepository.save(user);
//}
}
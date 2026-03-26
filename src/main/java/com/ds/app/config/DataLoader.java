package com.ds.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.entity.UserRole;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.repository.EmployeeRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            iAppUserRepository userRepo,
            EmployeeRepository employeeRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            //  Create Admin User (base class AppUser)
            if (!userRepo.existsByUsername("admin")) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);

                userRepo.save(admin);
                System.out.println(" Admin user created!");
            }

            //  Create Employee User
            if (!employeeRepo.existsByUsername("john")) {
                Employee emp = new Employee();
                emp.setUsername("john");
                emp.setPassword(passwordEncoder.encode("john123"));
                emp.setEmail("john@example.com");
                emp.setFirstName("John");
                emp.setLastName("Doe");
                emp.setRole(UserRole.EMPLOYEE);

                // Optional fields
                emp.setEmployeeExperience(null);
                emp.setCertificationStatus(null);
                emp.setSkillStatus(null);

                employeeRepo.save(emp);
                System.out.println("Employee user created!");
            }
            
            if (!employeeRepo.existsByUsername("ohn")) {
                Employee emp = new Employee();
                emp.setUsername("ohn");
                emp.setPassword(passwordEncoder.encode("ohn123"));
                emp.setEmail("john@example.com");
                emp.setFirstName("John");
                emp.setLastName("Doe");
                emp.setRole(UserRole.HR);

                // Optional fields
                emp.setEmployeeExperience(null);
                emp.setCertificationStatus(null);
                emp.setSkillStatus(null);

                employeeRepo.save(emp);
                System.out.println(" HR user created!");
            }
        };
    }
}
package com.ds.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.SkillStatus;
import com.ds.app.enums.UserRole;
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
            if (!employeeRepo.existsByUsername("suresh")) {
                Employee emp = new Employee();
                emp.setUsername("suresh");
                emp.setPassword(passwordEncoder.encode("suresh123"));
                emp.setEmail("suresh@example.com");
                emp.setFirstName("suresh");
                emp.setLastName("chand");
                emp.setRole(UserRole.EMPLOYEE); 
                emp.setEmployeeExperience(EmployeeExperience.FRESHER);

//                // Optional fields
               // emp.setEmployeeExperience(null);
                emp.setCertificationStatus(CertificationStatus.NON_CERTIFIED);
                emp.setSkillStatus(SkillStatus.NON_SKILLED);

                employeeRepo.save(emp);
                System.out.println("Employee user created!");
            }
            if (!employeeRepo.existsByUsername("mahesh")) {
                Employee emp = new Employee();
                emp.setUsername("mahesh");
                emp.setPassword(passwordEncoder.encode("mahesh123"));
                emp.setEmail("mahesh@example.com");
                emp.setFirstName("mahesh");
                emp.setLastName("chand");
                emp.setRole(UserRole.EMPLOYEE); 

                // Optional fields
                emp.setEmployeeExperience(EmployeeExperience.EXPERIENCED);
                emp.setCertificationStatus(CertificationStatus.CERTIFIED);
                emp.setSkillStatus(SkillStatus.SKILLED);

                employeeRepo.save(emp);
                System.out.println("Employee user created!");
            }
            if (!employeeRepo.existsByUsername("saurabh")) {
                Employee emp = new Employee();
                emp.setUsername("saurabh");
                emp.setPassword(passwordEncoder.encode("saurabh123"));
                emp.setEmail("saurabhkumar.mca24@bvicam.in");
                emp.setFirstName("saurabh");
                emp.setLastName("tiwari");
                emp.setRole(UserRole.EMPLOYEE); 

                // Optional fields
                emp.setEmployeeExperience(EmployeeExperience.EXPERIENCED);
                emp.setCertificationStatus(CertificationStatus.NON_CERTIFIED);
                emp.setSkillStatus(SkillStatus.NON_SKILLED);

                employeeRepo.save(emp);
                System.out.println("Employee user created!");
            }
            if (!employeeRepo.existsByUsername("mayank")) {
                Employee emp = new Employee();
                emp.setUsername("mayank");
                emp.setPassword(passwordEncoder.encode("mayank123"));
                emp.setEmail("mayank@example.com");
                emp.setFirstName("mayank");
                emp.setLastName("kumar");
                emp.setRole(UserRole.HR); 

                // Optional fields
                emp.setEmployeeExperience(EmployeeExperience.FRESHER);
                emp.setCertificationStatus(CertificationStatus.NON_CERTIFIED);
                emp.setSkillStatus(SkillStatus.NON_SKILLED);

                employeeRepo.save(emp);
                System.out.println("Employee user created!");
            }
            
            if (!employeeRepo.existsByUsername("john")) {
                Employee emp = new Employee();
                emp.setUsername("john");
                emp.setPassword(passwordEncoder.encode("john123"));
                emp.setEmail("john@example.com");
                emp.setFirstName("John");
                emp.setLastName("Doe");
                emp.setRole(UserRole.HR);

                // Optional fields
                emp.setEmployeeExperience(EmployeeExperience.EXPERIENCED);
                emp.setCertificationStatus(CertificationStatus.CERTIFIED);
                emp.setSkillStatus(SkillStatus.NON_SKILLED);

                employeeRepo.save(emp);
                System.out.println(" HR user created!");
            }
        };
    }
}
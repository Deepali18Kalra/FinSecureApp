package com.ds.app.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.entity.UserRole;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.iAppUserRepository;

//@Configuration
public class AppDataSeeder {

    @PersistenceContext
    private EntityManager entityManager;

  //  @Bean
    //@Transactional   
    CommandLineRunner syncEmployees(
            iAppUserRepository userRepo,
            EmployeeRepository employeeRepo,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepo.findByUsername("admin01").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin01");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepo.save(admin);
            }

            if (userRepo.findByUsername("hr01").isEmpty()) {
                AppUser hr = new AppUser();
                hr.setUsername("hr01");
                hr.setPassword(passwordEncoder.encode("hr123"));
                hr.setRole(UserRole.HR);
                userRepo.save(hr);
            }

            if (userRepo.findByUsername("employee01").isEmpty()) {
                AppUser emp = new AppUser();
                emp.setUsername("employee01");
                emp.setPassword(passwordEncoder.encode("emp123"));
                emp.setRole(UserRole.EMPLOYEE);
                userRepo.save(emp);
            }

            if (userRepo.findByUsername("employee02").isEmpty()) {
                AppUser emp = new AppUser();
                emp.setUsername("employee02");
                emp.setPassword(passwordEncoder.encode("emp123"));
                emp.setRole(UserRole.EMPLOYEE);
                userRepo.save(emp);
            }

            for (AppUser user : userRepo.findAll()) {

                if (user.getRole() == UserRole.EMPLOYEE) {

                    if (!employeeRepo.existsById(user.getUserId())) {

                        Employee employee = new Employee();
                        employee.setUserId(user.getUserId());
                        employee.setUsername(user.getUsername());
                        employee.setPassword(user.getPassword());
                        employee.setRole(user.getRole());
                        employee.setIsAccountLocked(user.getIsAccountLocked());
                        employee.setFailedLoginAttemptsCount(
                                user.getFailedLoginAttemptsCount()
                        );
                        employee.setHasActiveAssetEscalation(false);

                        entityManager.persist(employee); // ✅ now works
                    }
                }
            }
        };
    }
}


//package com.ds.app.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.ds.app.entity.AppUser;
//import com.ds.app.entity.Employee;
//import com.ds.app.entity.UserRole;
//import com.ds.app.repository.EmployeeRepository;
//import com.ds.app.repository.iAppUserRepository;
//
//@Configuration
//public class AppDataSeeder {
//
// @Bean
//    CommandLineRunner syncEmployees(
//            iAppUserRepository userRepo,
//            EmployeeRepository employeeRepo,
//            PasswordEncoder passwordEncoder) {
//
//        return args -> {
//            if (userRepo.findByUsername("admin01").isEmpty()) {
//                AppUser admin = new AppUser();
//                admin.setUsername("admin01");
//                admin.setPassword(passwordEncoder.encode("admin123"));
//                admin.setRole(UserRole.ADMIN);
//                userRepo.save(admin);
//            }
//
//            if (userRepo.findByUsername("hr01").isEmpty()) {
//                AppUser hr = new AppUser();
//                hr.setUsername("hr01");
//                hr.setPassword(passwordEncoder.encode("hr123"));
//                hr.setRole(UserRole.HR);
//                userRepo.save(hr);
//            }
//
//            if (userRepo.findByUsername("employee01").isEmpty()) {
//                AppUser emp = new AppUser();
//                emp.setUsername("employee01");
//                emp.setPassword(passwordEncoder.encode("emp123"));
//                emp.setRole(UserRole.EMPLOYEE);
//                userRepo.save(emp);
//            }
//
//            if (userRepo.findByUsername("employee02").isEmpty()) {
//                AppUser emp = new AppUser();
//                emp.setUsername("employee02");
//                emp.setPassword(passwordEncoder.encode("emp123"));
//                emp.setRole(UserRole.EMPLOYEE);
//                userRepo.save(emp);
//            }
//
//             for (AppUser user : userRepo.findAll()) {
//
//                if (user.getRole() == UserRole.EMPLOYEE) {
//
//                    boolean employeeExists =
//                            //employeeRepo.findByUserId(user.getUserId()).isPresent();
//
//employeeRepo.existsById(user.getUserId());
//
//                    if (!employeeExists) {
//                        Employee employee = new Employee();
//                        employee.setUserId(user.getUserId());
//                        employee.setUsername(user.getUsername());
//                        employee.setHasActiveAssetEscalation(false);
//
//                        employeeRepo.save(employee);
//
//                        System.out.println(
//                            "Employee row created for userId: " + user.getUserId()
//                        );
//                    }
//                }
//            }
//        };
//    }
//}
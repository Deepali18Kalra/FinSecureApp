package com.ds.app;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.enums.Status;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.iAppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            iAppUserRepository userRepo,
            EmployeeRepository employeeRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // ── ADMIN ─────────────────────────────────────────────────────────
            if (!userRepo.existsByUsername("admin")) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepo.save(admin);
                System.out.println("✅ Admin created — admin / admin123");
            }

            // ── FINANCE ───────────────────────────────────────────────────────
            if (!userRepo.existsByUsername("finance")) {
                AppUser finance = new AppUser();
                finance.setUsername("finance");
                finance.setPassword(passwordEncoder.encode("finance123"));
                finance.setRole(UserRole.FINANCE);
                userRepo.save(finance);
                System.out.println("✅ Finance created — finance / finance123");
            }

            // ── HR ────────────────────────────────────────────────────────────
            if (!employeeRepo.existsByUsername("hr")) {
                Employee hr = new Employee();
                hr.setUsername("hr");
                hr.setPassword(passwordEncoder.encode("hr123"));
                hr.setRole(UserRole.HR);
                hr.setFirstName("Priya");
                hr.setLastName("Sharma");
                hr.setEmail("sharmayatin0882@gmail.com");
                hr.setCurrentSalary(75000.0);
                hr.setEmployeeCode("HR001");
                hr.setStatus(Status.ACTIVE);    // ← enum instead of isActive
                employeeRepo.save(hr);
                System.out.println("✅ HR created — hr / hr123");
            }

            // ── EMPLOYEE 1 ────────────────────────────────────────────────────
            if (!employeeRepo.existsByUsername("rahul")) {
                Employee emp1 = new Employee();
                emp1.setUsername("rahul");
                emp1.setPassword(passwordEncoder.encode("rahul123"));
                emp1.setRole(UserRole.EMPLOYEE);
                emp1.setFirstName("Rahul");
                emp1.setEmail("sharmayatin0882@gmail.com");
                emp1.setLastName("Verma");
                emp1.setCurrentSalary(60000.0);
                emp1.setEmployeeCode("EMP001");
                emp1.setStatus(Status.ACTIVE);
                employeeRepo.save(emp1);
                System.out.println("✅ Employee 1 created — rahul / rahul123");
            }

            // ── EMPLOYEE 2 ────────────────────────────────────────────────────
            if (!employeeRepo.existsByUsername("sneha")) {
                Employee emp2 = new Employee();
                emp2.setUsername("sneha");
                emp2.setPassword(passwordEncoder.encode("sneha123"));
                emp2.setRole(UserRole.EMPLOYEE);
                emp2.setFirstName("Sneha");
                emp2.setLastName("Patel");
                emp2.setCurrentSalary(55000.0);
                emp2.setEmail("sharmayatin0882@gmail.com");
                emp2.setEmployeeCode("EMP002");
                emp2.setStatus(Status.ACTIVE);
                employeeRepo.save(emp2);
                System.out.println("✅ Employee 2 created — sneha / sneha123");
            }

            // ── EMPLOYEE 3 (inactive — salary job skips this) ─────────────────
            if (!employeeRepo.existsByUsername("inactive_emp")) {
                Employee emp3 = new Employee();
                emp3.setUsername("inactive_emp");
                emp3.setPassword(passwordEncoder.encode("inactive123"));
                emp3.setRole(UserRole.EMPLOYEE);
                emp3.setFirstName("Inactive");
                emp3.setLastName("User");
                emp3.setCurrentSalary(50000.0);
                emp3.setEmail("sharmayatin0882@gmail.com");
                emp3.setEmployeeCode("EMP003");
                emp3.setStatus(Status.INACTIVE);  // ← salary job skips this
                employeeRepo.save(emp3);
                System.out.println("✅ Inactive employee created — inactive_emp / inactive123");
            }
            if (!employeeRepo.existsByUsername("ASHISH")) {
                Employee emp3 = new Employee();
                emp3.setUsername("ASHISH");
                emp3.setPassword(passwordEncoder.encode("ASHISH123"));
                emp3.setRole(UserRole.EMPLOYEE);
                emp3.setFirstName("ASHISH");
                emp3.setLastName("BANSAL");
                emp3.setCurrentSalary(50000.0);
                emp3.setEmail("sharmayatin0882@gmail.com");
                emp3.setEmployeeCode("EMP004");
                emp3.setStatus(Status.ACTIVE);  // ← salary job skips this
                employeeRepo.save(emp3);
                System.out.println("✅ Inactive employee created — inactive_emp / inactive123");
            }
            if (!employeeRepo.existsByUsername("Sakshi")) {
                Employee emp3 = new Employee();
                emp3.setUsername("Sakshi");
                emp3.setPassword(passwordEncoder.encode("Sakshi123"));
                emp3.setRole(UserRole.EMPLOYEE);
                emp3.setFirstName("Sakshi");
                emp3.setLastName("Sakshi");
                emp3.setCurrentSalary(50000.0);
                emp3.setEmail("sharmayatin0882@gmail.com");
                emp3.setEmployeeCode("EMP005");
                emp3.setStatus(Status.ACTIVE);  // ← salary job skips this
                employeeRepo.save(emp3);
                System.out.println("✅ Inactive employee created — inactive_emp / inactive123");
            }
        };
    }
}
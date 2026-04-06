package com.ds.app;

import com.ds.app.entity.*;
import com.ds.app.enums.*;
import com.ds.app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final IEmployeeRepository employeeRepo;
    private final IAttendanceRepository attendanceRepo;
    private final ILeaveBalanceRepository leaveBalanceRepo;
    private final ILeaveRepository leaveRepo;
    private final IRegularizationRequestRepository regularizationRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (employeeRepo.count() > 0) {
            log.info("Database already populated. Skipping demo data seeder.");
            return;
        }

        log.info("Starting massive data generation (Users, Balances, Attendance, Leaves, Regularizations)...");
        String defaultPass = passwordEncoder.encode("password123");

        // 1. Create HR
        Employee hr = createEmployee("Sarah", "Connor", "hr@company.com", "sarah_hr", defaultPass, UserRole.HR, null);

        // 2. Create 3 Managers
        Employee m1 = createEmployee("Michael", "Scott", "mscott@company.com", "mscott", defaultPass, UserRole.MANAGER, hr);
        Employee m2 = createEmployee("Ron", "Swanson", "rswanson@company.com", "rswanson", defaultPass, UserRole.MANAGER, hr);
        Employee m3 = createEmployee("Leslie", "Knope", "lknope@company.com", "lknope", defaultPass, UserRole.MANAGER, hr);
        List<Employee> managers = List.of(m1, m2, m3);

        // 3. Create 15 Employees distributed under Managers
        List<Employee> allStaff = new ArrayList<>();
        allStaff.add(hr);
        allStaff.addAll(managers);

        for (int i = 1; i <= 15; i++) {
            Employee managerForThisEmp = managers.get(i % 3);
            Employee emp = createEmployee("Employee" + i, "Test" + i, "emp" + i + "@company.com", "emp" + i, defaultPass, UserRole.EMPLOYEE, managerForThisEmp);
            allStaff.add(emp);
        }

        // 4. Seed Balances
        seedLeaveBalances(allStaff, LocalDate.now().getYear());

        // 5. Seed Daily Records (Attendance, Leaves, Regularizations)
        seedDailyRecords(allStaff, LocalDate.of(2026, 3, 1));

        log.info("Demo data seeding complete! You can log in with any username and password: 'password123'");
    }

    private Employee createEmployee(String first, String last, String email, String username, String pass, UserRole role, Employee manager) {
        Employee emp = new Employee();
        // Base AppUser fields
        emp.setUsername(username);
        emp.setPassword(pass);
        emp.setRole(role);
        emp.setFailedLoginAttemptsCount(0);
        emp.setIsAccountLocked(false);
        // Employee specific fields
        emp.setFirstName(first);
        emp.setLastName(last);
        emp.setEmail(email);
        emp.setManager(manager);

        return employeeRepo.save(emp);
    }

    private void seedLeaveBalances(List<Employee> employees, int year) {
        List<LeaveBalance> balances = new ArrayList<>();
        for (Employee emp : employees) {
            LeaveBalance balance = LeaveBalance.builder()
                    .employee(emp)
                    .year(year)
                    .casualLeaveBalance(8)
                    .sickLeaveBalance(10)
                    .earnedLeaveBalance(BigDecimal.valueOf(12.5))
                    .build();
            balances.add(balance);
        }
        leaveBalanceRepo.saveAll(balances);
    }

    private void seedDailyRecords(List<Employee> employees, LocalDate startDate) {
        LocalDate endDate = LocalDate.now().minusDays(1);
        Random random = new Random();

        List<Attendance> attendanceBatch = new ArrayList<>();
        List<Leave> leaveBatch = new ArrayList<>();
        List<RegularizationRequest> regBatch = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Skip Weekends
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            for (Employee emp : employees) {
                int chance = random.nextInt(100);

                if (chance < 80) {
                    // 80% chance: Normal Present Day
                    attendanceBatch.add(Attendance.builder()
                            .employee(emp).date(date).status(AttendanceStatus.PRESENT)
                            .punchInTime(LocalTime.of(9, random.nextInt(15)))
                            .punchOutTime(LocalTime.of(18, random.nextInt(30)))
                            .totalMinutesWorked(540).isLate(false).isRegularized(false)
                            .build());

                } else if (chance < 85) {
                    // 5% chance: Late Arrival
                    attendanceBatch.add(Attendance.builder()
                            .employee(emp).date(date).status(AttendanceStatus.PRESENT)
                            .punchInTime(LocalTime.of(10, 30))
                            .punchOutTime(LocalTime.of(18, 0))
                            .totalMinutesWorked(450).isLate(true).isRegularized(false)
                            .build());

                } else if (chance < 90) {
                    // 5% chance: Missed Swipe + Create a PENDING Regularization Request
                    attendanceBatch.add(Attendance.builder()
                            .employee(emp).date(date).status(AttendanceStatus.MISS_SWIPE)
                            .punchInTime(LocalTime.of(9, 0)).punchOutTime(null)
                            .totalMinutesWorked(0).isLate(false).isRegularized(false)
                            .build());

                    // Generate the regularization request for the manager to approve later
                    regBatch.add(RegularizationRequest.builder()
                            .employee(emp).date(date).reason("Forgot to punch out due to client meeting")
                            .punchInTime(LocalTime.of(9, 0)).punchOutTime(LocalTime.of(18, 0))
                            .status(RegularizationRequestStatus.PENDING)
                            .build());

                } else {
                    // 10% chance: On Leave (Randomly Sick or Casual)
                    LeaveType type = random.nextBoolean() ? LeaveType.SICK : LeaveType.CASUAL;
                    boolean isApproved = random.nextBoolean(); // Mix of approved and pending leaves

                    attendanceBatch.add(Attendance.builder()
                            .employee(emp).date(date).status(AttendanceStatus.ABSENT)
                            .totalMinutesWorked(0).isLate(false).isRegularized(false)
                            .build());

                    leaveBatch.add(Leave.builder()
                            .employee(emp).startDate(date).endDate(date).totalDays(1)
                            .reasonForLeave("Not feeling well / Personal work")
                            .leaveType(type)
                            .status(isApproved ? LeaveStatus.APPROVED : LeaveStatus.PENDING)
                            .approvedBy(isApproved ? emp.getManager() : null)
                            .approvalDate(isApproved ? date.minusDays(1) : null)
                            .build());
                }
            }
        }

        // Save everything to the database in bulk
        attendanceRepo.saveAll(attendanceBatch);
        leaveRepo.saveAll(leaveBatch);
        regularizationRepo.saveAll(regBatch);
    }
}
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final IEmployeeRepository employeeRepo;
    private final IAttendanceRepository attendanceRepo;
    private final ILeaveBalanceRepository leaveBalanceRepo;
    private final ILeaveRepository leaveRepo;
    private final IRegularizationRequestRepository regularizationRepo;
    private final IHolidayRepository holidayRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (employeeRepo.count() > 0) {
            log.info("Database already populated. Skipping demo data seeder.");
            return;
        }

        log.info("Starting demo data generation (7 Users, Holidays, Synced Balances, Attendance)...");
        String defaultPass = passwordEncoder.encode("password123");

        // 1. Seed Holidays for the timeframe
        seedHolidays();

        // 2. Create Employees with Realistic Names
        Employee hr = createEmployee("Emily", "Chen", "echen@company.com", "echen_hr", defaultPass, UserRole.HR, null);

        Employee mgr1 = createEmployee("David", "Miller", "dmiller@company.com", "dmiller", defaultPass, UserRole.MANAGER, hr);
        Employee mgr2 = createEmployee("Sarah", "Jenkins", "sjenkins@company.com", "sjenkins", defaultPass, UserRole.MANAGER, hr);

        Employee emp1 = createEmployee("Michael", "Ross", "mross@company.com", "mross", defaultPass, UserRole.EMPLOYEE, mgr1);
        Employee emp2 = createEmployee("Rachel", "Zane", "rzane@company.com", "rzane", defaultPass, UserRole.EMPLOYEE, mgr1);

        Employee emp3 = createEmployee("Harvey", "Specter", "hspecter@company.com", "hspecter", defaultPass, UserRole.EMPLOYEE, mgr2);
        Employee emp4 = createEmployee("Donna", "Paulsen", "dpaulsen@company.com", "dpaulsen", defaultPass, UserRole.EMPLOYEE, mgr2);

        List<Employee> allStaff = List.of(hr, mgr1, mgr2, emp1, emp2, emp3, emp4);

        // 3. Initialize Base Leave Balances in memory
        Map<Long, LeaveBalance> employeeBalances = initializeLeaveBalances(allStaff, LocalDate.now().getYear());

        // 4. Seed Daily Records (Attendance, Leaves, Regularizations)
        seedDailyRecords(allStaff, employeeBalances, LocalDate.of(2026, 3, 1));

        // 5. Save the final Leave Balances AFTER consumption logic is applied
        leaveBalanceRepo.saveAll(employeeBalances.values());

        log.info("Demo data seeding complete! All passwords are 'password123'.");
    }

    private void seedHolidays() {
        List<Holiday> holidays = List.of(
                Holiday.builder().date(LocalDate.of(2026, 3, 3)).name("Holi").type(HolidayType.NATIONAL).build(),
                Holiday.builder().date(LocalDate.of(2026, 3, 30)).name("Company Foundation Day").type(HolidayType.OPTIONAL).build()
        );
        holidayRepo.saveAll(holidays);
    }

    private Employee createEmployee(String first, String last, String email, String username, String pass, UserRole role, Employee manager) {
        Employee emp = new Employee();
        emp.setUsername(username);
        emp.setPassword(pass);
        emp.setRole(role);
        emp.setFailedLoginAttemptsCount(0);
        emp.setIsAccountLocked(false);
        emp.setFirstName(first);
        emp.setLastName(last);
        emp.setEmail(email);
        emp.setManager(manager);

        return employeeRepo.save(emp);
    }

    private Map<Long, LeaveBalance> initializeLeaveBalances(List<Employee> employees, int year) {
        Map<Long, LeaveBalance> map = new HashMap<>();
        for (Employee emp : employees) {
            LeaveBalance balance = LeaveBalance.builder()
                    .employee(emp)
                    .year(year)
                    .casualLeaveBalance(8)
                    .sickLeaveBalance(10)
                    .earnedLeaveBalance(BigDecimal.valueOf(12.5))
                    .casualLeavesConsumed(0)
                    .sickLeavesConsumed(0)
                    .build();
            map.put(emp.getUserId(), balance);
        }
        return map;
    }

    private void seedDailyRecords(List<Employee> employees, Map<Long, LeaveBalance> balancesMap, LocalDate startDate) {
        LocalDate endDate = LocalDate.now().minusDays(1);
        Random random = new Random();

        List<Attendance> attendanceBatch = new ArrayList<>();
        List<Leave> leaveBatch = new ArrayList<>();
        List<RegularizationRequest> regBatch = new ArrayList<>();
        List<LocalDate> holidayDates = holidayRepo.findAll().stream().map(Holiday::getDate).toList();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Skip Weekends and Holidays
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY || holidayDates.contains(date)) {
                continue;
            }

            for (Employee emp : employees) {
                int chance = random.nextInt(100);

                if (chance < 80) {
                    // 80% chance: Present
                    attendanceBatch.add(createAttendance(emp, date, AttendanceStatus.PRESENT, LocalTime.of(9, random.nextInt(15)), LocalTime.of(18, random.nextInt(30)), 540, false));
                } else if (chance < 85) {
                    // 5% chance: Late
                    attendanceBatch.add(createAttendance(emp, date, AttendanceStatus.PRESENT, LocalTime.of(10, 30), LocalTime.of(18, 0), 450, true));
                } else if (chance < 90) {
                    // 5% chance: Missed Swipe -> Pending Regularization
                    attendanceBatch.add(createAttendance(emp, date, AttendanceStatus.MISS_SWIPE, LocalTime.of(9, 0), null, 0, false));
                    regBatch.add(RegularizationRequest.builder()
                            .employee(emp).date(date).reason("Forgot to punch out").punchInTime(LocalTime.of(9, 0)).punchOutTime(LocalTime.of(18, 0))
                            .status(RegularizationRequestStatus.PENDING).build());
                } else {
                    // 10% chance: Leave
                    LeaveType type = random.nextBoolean() ? LeaveType.SICK : LeaveType.CASUAL;
                    boolean isApproved = random.nextBoolean(); // Half are approved, half pending for managers to review

                    attendanceBatch.add(createAttendance(emp, date, AttendanceStatus.ABSENT, null, null, 0, false));

                    leaveBatch.add(Leave.builder()
                            .employee(emp).startDate(date).endDate(date).totalDays(1)
                            .reasonForLeave(type == LeaveType.SICK ? "Fever" : "Personal Errand")
                            .leaveType(type).status(isApproved ? LeaveStatus.APPROVED : LeaveStatus.PENDING)
                            .approvedBy(isApproved ? emp.getManager() : null).approvalDate(isApproved ? date.minusDays(1) : null)
                            .build());

                    // If the leave is APPROVED, deduct it from their balance instantly
                    if (isApproved) {
                        LeaveBalance b = balancesMap.get(emp.getUserId());
                        if (type == LeaveType.SICK) {
                            b.setSickLeaveBalance(b.getSickLeaveBalance() - 1);
                            b.setSickLeavesConsumed(b.getSickLeavesConsumed() + 1);
                        } else {
                            b.setCasualLeaveBalance(b.getCasualLeaveBalance() - 1);
                            b.setCasualLeavesConsumed(b.getCasualLeavesConsumed() + 1);
                        }
                    }
                }
            }
        }

        attendanceRepo.saveAll(attendanceBatch);
        leaveRepo.saveAll(leaveBatch);
        regularizationRepo.saveAll(regBatch);
    }

    private Attendance createAttendance(Employee emp, LocalDate date, AttendanceStatus status, LocalTime in, LocalTime out, int mins, boolean isLate) {
        return Attendance.builder().employee(emp).date(date).status(status).punchInTime(in).punchOutTime(out).totalMinutesWorked(mins).isLate(isLate).isRegularized(false).build();
    }
}
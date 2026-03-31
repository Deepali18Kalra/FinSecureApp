package com.ds.app;

import com.ds.app.entity.*;
import com.ds.app.enums.*;
import com.ds.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final iAppUserRepository appUserRepository;
    private final IAttendanceRepository attendanceRepository;
    private final ILeaveRepository leaveRepository;
    private final ILeaveBalanceRepository leaveBalanceRepository;
    private final IHolidayRepository holidayRepository;
    private final ITimesheetRepository timesheetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (appUserRepository.count() > 0) {
            System.out.println("Data already seeded, skipping...");
            return;
        }

        int currentYear = Year.now().getValue();

        // 1) HRs
        Employee hr1 = buildUser("manish_hr", "pass123hr", "Manish", "Sharma", UserRole.HR, null);
        Employee hr2 = buildUser("neha_hr", "pass123hr", "Neha", "Verma", UserRole.HR, null);
        hr1 = (Employee) appUserRepository.save(hr1);
        hr2 = (Employee) appUserRepository.save(hr2);

        // 2) Employees
        Employee e1 = buildUser("mayank", "pass123", "Mayank", "Singh", UserRole.EMPLOYEE, hr1);
        Employee e2 = buildUser("riya", "pass123", "Riya", "Kapoor", UserRole.EMPLOYEE, hr1);
        Employee e3 = buildUser("arjun", "pass123", "Arjun", "Mehta", UserRole.EMPLOYEE, hr1);
        Employee e4 = buildUser("kavya", "pass123", "Kavya", "Nair", UserRole.EMPLOYEE, hr2);
        Employee e5 = buildUser("rohit", "pass123", "Rohit", "Gupta", UserRole.EMPLOYEE, hr2);
        Employee e6 = buildUser("sana", "pass123", "Sana", "Khan", UserRole.EMPLOYEE, hr2);

        List<Employee> employees = appUserRepository.saveAll(List.of(e1, e2, e3, e4, e5, e6))
                .stream().map(u -> (Employee) u).toList();

        // 3) Holidays
        seedHolidays(currentYear);

        // 4) Initial LeaveBalance for each employee
        for (Employee emp : employees) {
            leaveBalanceRepository.save(
                    LeaveBalance.builder()
                            .employee(emp)
                            .year(currentYear)
                            .sickLeaveBalance(10)
                            .casualLeaveBalance(8)
                            .earnedLeaveBalance(BigDecimal.ZERO)
                            .reservedSickLeaves(0)
                            .reservedCasualLeaves(0)
                            .reservedEarnedLeaves(0)
                            .sickLeavesConsumed(0)
                            .casualLeavesConsumed(0)
                            .earnedLeavesConsumed(0)
                            .carriedForwardEarnedDays(BigDecimal.ZERO)
                            .build()
            );
        }

        // 5) Attendance + Leave + Timesheet sample
        for (Employee emp : employees) {
            seedAttendance(emp);
            seedLeavesAndSyncBalance(emp, emp.getHr(), currentYear);
            seedPreviousMonthTimesheet(emp);
        }

        System.out.println("✅ Seeding complete!");
        System.out.println("HR Login → username: manish_hr | password: pass123hr");
        System.out.println("HR Login → username: neha_hr   | password: pass123hr");
        System.out.println("EMP Login → username: mayank   | password: pass123");
    }

    private Employee buildUser(String username, String rawPassword, String firstName, String lastName,
                               UserRole role, Employee hr) {
        Employee user = new Employee();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setFailedLoginAttemptsCount(0);
        user.setIsAccountLocked(false);
        user.setHr(hr);
        return user;
    }

    private void seedAttendance(Employee employee) {
        Attendance missSwipeToday = Attendance.builder()
                .employee(employee)
                .date(LocalDate.now())
                .punchInTime(LocalTime.of(9, 20))
                .punchOutTime(null)
                .status(AttendanceStatus.MISS_SWIPE)
                .hoursWorked(0.0)
                .isRegularized(false)
                .build();

        Attendance presentYesterday = Attendance.builder()
                .employee(employee)
                .date(LocalDate.now().minusDays(1))
                .punchInTime(LocalTime.of(9, 5))
                .punchOutTime(LocalTime.of(18, 15))
                .status(AttendanceStatus.PRESENT)
                .hoursWorked(9.17)
                .isRegularized(false)
                .build();

        Attendance halfDay = Attendance.builder()
                .employee(employee)
                .date(LocalDate.now().minusDays(2))
                .punchInTime(LocalTime.of(10, 0))
                .punchOutTime(LocalTime.of(13, 0))
                .status(AttendanceStatus.HALF_DAY_PRESENT)
                .hoursWorked(3.0)
                .isRegularized(false)
                .build();

        Attendance absent = Attendance.builder()
                .employee(employee)
                .date(LocalDate.now().minusDays(3))
                .status(AttendanceStatus.ABSENT)
                .hoursWorked(0.0)
                .isRegularized(false)
                .build();

        attendanceRepository.saveAll(List.of(missSwipeToday, presentYesterday, halfDay, absent));
    }

    private void seedLeavesAndSyncBalance(Employee employee, Employee approverHr, int year) {
        Leave pending = Leave.builder()
                .employee(employee)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(3))
                .totalDays(2)
                .leaveType(LeaveType.CASUAL)
                .reasonForLeave("Personal work")
                .status(LeaveStatus.PENDING)
                .build();

        Leave approved = Leave.builder()
                .employee(employee)
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(9))
                .totalDays(2)
                .leaveType(LeaveType.SICK)
                .reasonForLeave("Fever")
                .status(LeaveStatus.APPROVED)
                .approvedBy(approverHr)
                .approvalDate(LocalDate.now().minusDays(11))
                .build();

        Leave rejected = Leave.builder()
                .employee(employee)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(5))
                .totalDays(1)
                .leaveType(LeaveType.CASUAL)
                .reasonForLeave("Family function")
                .status(LeaveStatus.REJECTED)
                .approvedBy(approverHr)
                .approvalDate(LocalDate.now().minusDays(6))
                .rejectionReason("Team bandwidth is low for that date")
                .build();

        leaveRepository.saveAll(List.of(pending, approved, rejected));

        LeaveBalance lb = leaveBalanceRepository.findByEmployeeUserIdAndYear(employee.getUserId(), year)
                .orElseThrow(() -> new IllegalStateException("Leave balance missing for user: " + employee.getUserId()));

        // pending casual (2) => reserve
        lb.setReservedCasualLeaves(lb.getReservedCasualLeaves() + 2);

        // approved sick (2) => deduct + consumed
        lb.setSickLeaveBalance(lb.getSickLeaveBalance() - 2);
        lb.setSickLeavesConsumed(lb.getSickLeavesConsumed() + 2);

        leaveBalanceRepository.save(lb);
    }

    private void seedHolidays(int year) {
        holidayRepository.saveAll(List.of(
                Holiday.builder().date(LocalDate.of(year, 1, 26)).name("Republic Day").type(HolidayType.NATIONAL).build(),
                Holiday.builder().date(LocalDate.of(year, 8, 15)).name("Independence Day").type(HolidayType.NATIONAL).build(),
                Holiday.builder().date(LocalDate.of(year, 10, 2)).name("Gandhi Jayanti").type(HolidayType.NATIONAL).build(),
                Holiday.builder().date(LocalDate.of(year, 11, 14)).name("Children's Day").type(HolidayType.OPTIONAL).build()
        ));
    }

    private void seedPreviousMonthTimesheet(Employee employee) {
        YearMonth prev = YearMonth.now().minusMonths(1);

        Timesheet ts = Timesheet.builder()
                .employee(employee)
                .month(prev.getMonthValue())
                .year(prev.getYear())
                .status(TimesheetStatus.APPROVED)
                .submittedAt(prev.atEndOfMonth().atTime(18, 0))
                .approvedBy(employee.getHr())
                .approvalDate(LocalDate.now().minusDays(2))
                .totalMonthlyHours(0.0)
                .timesheetEntries(new ArrayList<>())
                .build();

        List<TimesheetEntry> entries = new ArrayList<>();
        entries.add(TimesheetEntry.builder().timesheet(ts).date(prev.atDay(3)).taskDescription("Backend API").hoursWorked(8.0).projectId(101L).projectName("DMS").build());
        entries.add(TimesheetEntry.builder().timesheet(ts).date(prev.atDay(4)).taskDescription("Bug fixing").hoursWorked(3.5).projectId(101L).projectName("DMS").build());
        entries.add(TimesheetEntry.builder().timesheet(ts).date(prev.atDay(5)).taskDescription("Unit testing").hoursWorked(6.0).projectId(102L).projectName("EMS").build());

        double totalHours = entries.stream().mapToDouble(TimesheetEntry::getHoursWorked).sum();

        ts.setTimesheetEntries(entries);
        ts.setTotalMonthlyHours(totalHours);

        // single save (cascade persists entries)
        timesheetRepository.save(ts);
    }
}
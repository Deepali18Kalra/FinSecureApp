package com.ds.app;

import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.enums.LeaveType;
import com.ds.app.enums.UserRole;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.repository.ILeaveRepository;
import com.ds.app.repository.iAppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final iAppUserRepository appUserRepository;
    private final IAttendanceRepository attendanceRepository;
    private final ILeaveRepository leaveRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // skip if already seeded
        if (appUserRepository.count() > 0) {
            System.out.println("Data already seeded, skipping...");
            return;
        }

        // =========================
        // 1) CREATE HRs
        // =========================
        Employee hr1 = buildUser("manish_hr", "pass123hr", "Manish", "Sharma", UserRole.HR, null);
        Employee hr2 = buildUser("neha_hr", "pass123hr", "Neha", "Verma", UserRole.HR, null);

        hr1 = (Employee) appUserRepository.save(hr1);
        hr2 = (Employee) appUserRepository.save(hr2);

        System.out.println("HR created: " + hr1.getUsername() + " | id: " + hr1.getUserId());
        System.out.println("HR created: " + hr2.getUsername() + " | id: " + hr2.getUserId());

        // =========================
        // 2) CREATE EMPLOYEES UNDER HRs
        // =========================
        Employee e1 = buildUser("mayank", "pass123", "Mayank", "Singh", UserRole.EMPLOYEE, hr1);
        Employee e2 = buildUser("riya", "pass123", "Riya", "Kapoor", UserRole.EMPLOYEE, hr1);
        Employee e3 = buildUser("arjun", "pass123", "Arjun", "Mehta", UserRole.EMPLOYEE, hr1);

        Employee e4 = buildUser("kavya", "pass123", "Kavya", "Nair", UserRole.EMPLOYEE, hr2);
        Employee e5 = buildUser("rohit", "pass123", "Rohit", "Gupta", UserRole.EMPLOYEE, hr2);
        Employee e6 = buildUser("sana", "pass123", "Sana", "Khan", UserRole.EMPLOYEE, hr2);

        List<Employee> employees = appUserRepository.saveAll(List.of(e1, e2, e3, e4, e5, e6))
                .stream()
                .map(user -> (Employee) user)
                .toList();

        employees.forEach(emp ->
                System.out.println("Employee created: " + emp.getUsername() + " | id: " + emp.getUserId()
                        + " | HR: " + emp.getHr().getUsername())
        );

        // =========================
        // 3) ATTENDANCE FOR EACH EMPLOYEE (last 4 days)
        // =========================
        for (Employee emp : employees) {
            seedAttendance(emp);
        }

        // =========================
        // 4) LEAVE REQUESTS FOR TESTING
        // =========================
        // For each employee: create 3 leave requests -> PENDING, APPROVED, REJECTED
        for (Employee emp : employees) {
            seedLeaves(emp, emp.getHr());
        }

        System.out.println("✅ Seeding complete!");
        System.out.println("HR Login → username: manish_hr | password: pass123hr");
        System.out.println("HR Login → username: neha_hr   | password: pass123hr");
        System.out.println("EMP Login → username: mayank   | password: pass123");
        System.out.println("EMP Login → username: riya     | password: pass123");
        System.out.println("EMP Login → username: arjun    | password: pass123");
        System.out.println("EMP Login → username: kavya    | password: pass123");
        System.out.println("EMP Login → username: rohit    | password: pass123");
        System.out.println("EMP Login → username: sana     | password: pass123");
    }

    // -----------------------------------------
    // Helper: Build Employee/HR
    // -----------------------------------------
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

    // -----------------------------------------
    // Helper: Seed attendance
    // -----------------------------------------
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

        // replaced LATE with PRESENT
        Attendance presentTwoDaysAgo = Attendance.builder()
                .employee(employee)
                .date(LocalDate.now().minusDays(2))
                .punchInTime(LocalTime.of(9, 30))
                .punchOutTime(LocalTime.of(18, 0))
                .status(AttendanceStatus.PRESENT)
                .hoursWorked(8.5)
                .isRegularized(false)
                .build();

        Attendance absentThreeDaysAgo = Attendance.builder()
                .employee(employee)
                .date(LocalDate.now().minusDays(3))
                .punchInTime(null)
                .punchOutTime(null)
                .status(AttendanceStatus.ABSENT)
                .hoursWorked(0.0)
                .isRegularized(false)
                .build();

        attendanceRepository.saveAll(List.of(
                missSwipeToday, presentYesterday, presentTwoDaysAgo, absentThreeDaysAgo
        ));

        System.out.println("Attendance seeded for: " + employee.getUsername());
    }

    // -----------------------------------------
    // Helper: Seed leaves
    // -----------------------------------------
    private void seedLeaves(Employee employee, Employee approverHr) {

        Leave pending = Leave.builder()
                .employee(employee)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(3))
                .totalDays(2)
                .leaveType(LeaveType.CASUAL)
                .reasonForLeave("Personal work")
                .status(LeaveStatus.PENDING)
                .approvedBy(null)
                .approvalDate(null)
                .rejectionReason(null)
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
                .rejectionReason(null)
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

        System.out.println("Leaves seeded for: " + employee.getUsername()
                + " (PENDING/APPROVED/REJECTED)");
    }
}
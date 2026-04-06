package com.ds.app.scheduler;

import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.repository.IEmployeeRepository;
import com.ds.app.service.IHolidayService;
// ... (imports for your leave service)
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final IEmployeeRepository employeeRepo;
    private final IAttendanceRepository attendanceRepo;
    private final IHolidayService holidayService;
    // private final ILeaveService leaveService; // You will add this later

    @Scheduled(cron = "0 55 23 * * ?")
    public void markAbsentEmployees() {
        LocalDate today = LocalDate.now();

        if (isWeekend(today)) return;
        if (holidayService.isHoliday(today)) return;

        // Note: You would need a custom query in your EmployeeRepo to fetch active employees
        List<Employee> allEmployees = employeeRepo.findAll();

        for (Employee emp : allEmployees) {

            boolean punchedIn = attendanceRepo.existsByEmployee_UserIdAndDate(emp.getUserId(), today);

            // boolean onLeave = leaveService.isOnApprovedLeave(emp.getUserId(), today); // Add later
            boolean onLeave = false;

            if (!punchedIn && !onLeave) {
                Attendance absentRecord = Attendance.builder()
                        .employee(emp)
                        .date(today)
                        .status(AttendanceStatus.ABSENT)
                        .build();

                attendanceRepo.save(absentRecord);
            }
        }
    }

    // Your helper method!
    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
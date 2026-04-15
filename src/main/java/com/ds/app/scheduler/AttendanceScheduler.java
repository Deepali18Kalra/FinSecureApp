package com.ds.app.scheduler;

import com.ds.app.entity.Employee;
import com.ds.app.repository.IEmployeeRepository;
import com.ds.app.service.IAttendanceService;
import com.ds.app.service.IHolidayService;
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
    private final IHolidayService holidayService;
    private final IAttendanceService attendanceService;

    @Scheduled(cron = "0 55 23 * * ?")
    public void markAbsentEmployees() {
        LocalDate today = LocalDate.now();

        if (isWeekend(today)) return;
        if (holidayService.isHoliday(today)) return;

        List<Employee> allAbsentEmployees = employeeRepo.findAbsentEmployeesByDate(today);

        for (Employee employee : allAbsentEmployees) {
            attendanceService.markEmployeeAbsent(employee, today);
        }
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
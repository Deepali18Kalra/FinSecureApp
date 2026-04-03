package com.ds.app.service.Impl;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.exception.ForbiddenException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.AttendanceMapper;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.repository.IEmployeeRepository;
import com.ds.app.service.IAttendanceService;
import com.ds.app.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService{

    private final SecurityUtils securityUtil;
    private final IAttendanceRepository attendanceRepo;
    private final IEmployeeRepository employeeRepo;
    private final AttendanceMapper attendanceMapper;

    // Employee related methods

    @Override
    public AttendanceResponse punchIn() {
        Employee loggedInEmp = securityUtil.getLoggedInEmployee();

        Attendance savedAttendance = attendanceRepo.findByEmployeeUserIdAndDate(loggedInEmp.getUserId(),LocalDate.now())
                .orElseGet(() -> {
                    Attendance todayAttendance = Attendance.builder()
                            .employee(loggedInEmp)
                            .date(LocalDate.now())
                            .punchInTime(LocalTime.now())
                            .build();
                    return attendanceRepo.save(todayAttendance);
                });

        return attendanceMapper.mapToResponse(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceResponse punchOut() {
        Employee loggedInEmp = securityUtil.getLoggedInEmployee();
        Attendance todayAttendance = attendanceRepo.findByEmployeeUserIdAndDate(loggedInEmp.getUserId(), LocalDate.now())
                .orElseGet( () -> {
                    Attendance newTodayAttendance = Attendance.builder()
                            .employee(loggedInEmp)
                            .date(LocalDate.now())
                            .build();
                    return attendanceRepo.save(newTodayAttendance);
                });
        if (todayAttendance.getPunchOutTime() != null) {
            return attendanceMapper.mapToResponse(todayAttendance);
        }

        todayAttendance.setPunchOutTime(LocalTime.now());

        if(todayAttendance.getPunchInTime() != null) {
            Duration duration = Duration.between(todayAttendance.getPunchInTime(), todayAttendance.getPunchOutTime());
            double hoursWorked = duration.toMillis() / 3600000.0;
            todayAttendance.setHoursWorked(hoursWorked);

            AttendanceStatus todayStatus;
            if(hoursWorked >= 4) {
                todayStatus = AttendanceStatus.PRESENT;
            }else {
                todayStatus = AttendanceStatus.HALF_DAY_PRESENT;
            }
            todayAttendance.setStatus(todayStatus);
        }

        return attendanceMapper.mapToResponse(todayAttendance);
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(Integer month, Integer year) {
        Employee loggedInEmp = securityUtil.getLoggedInEmployee();
        List<Attendance> attendanceList = attendanceRepo.findAttendanceByEmployeeUserIdAndMonthAndYear(loggedInEmp.getUserId(), month, year);
        return attendanceList.stream()
                .map(attendance -> attendanceMapper.mapToResponse(attendance))
                .toList();
    }

    @Override
    public AttendanceResponse getMyAttendanceByDate(LocalDate date) {
        Employee emp = securityUtil.getLoggedInEmployee();

        Attendance todayAttendance = attendanceRepo.findByEmployeeUserIdAndDate(emp.getUserId(), date)
                .orElseThrow( () -> new ResourceNotFoundException("Attendance not found on date: " + date));
        return attendanceMapper.mapToResponse(todayAttendance);
    }

    // MANAGER related methods

    @Override
    public Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Integer month, Integer year, Pageable pageable) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() ->new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Employee loggedEmployee = securityUtil.getLoggedInEmployee();

        if(!emp.getManager().getUserId().equals(loggedEmployee.getUserId())) {
            throw new ForbiddenException("You are not allowed to view attendance for employee id: " + employeeId);
        }
        Page<Attendance> attendancePage = attendanceRepo.findAttendanceByEmployeeUserIdAndMonthAndYear(employeeId, month, year, pageable);
        return attendancePage.map(attendance -> attendanceMapper.mapToResponse(attendance));
    }

    @Override
    public Page<AttendanceResponse> getAllAttendanceByDate(LocalDate date, Pageable pageable) {
        Employee loggedInHr = securityUtil.getLoggedInEmployee();
        Page<Attendance> attendanceByDatePage = attendanceRepo.findByEmployee_Manager_UserIdAndDate(loggedInHr.getUserId(),date, pageable);
        return attendanceByDatePage.map(attendance -> attendanceMapper.mapToResponse(attendance));
    }
}
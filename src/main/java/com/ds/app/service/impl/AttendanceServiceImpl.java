package com.ds.app.service.impl;

import com.ds.app.dto.response.AttendanceResponse;
import com.ds.app.dto.response.MonthlyAttendanceReport;
import com.ds.app.dto.response.TeamAttendanceReportRow;
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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements IAttendanceService {

    private final SecurityUtils securityUtil;
    private final IAttendanceRepository attendanceRepo;
    private final IEmployeeRepository employeeRepo;
    private final AttendanceMapper attendanceMapper;

    // Employee related methods

    @Override
    @Transactional
    public AttendanceResponse punchIn() {
        Employee loggedInEmp = securityUtil.getLoggedInEmployee();
        log.info("Punch-in requested. employeeId={}, date={}", loggedInEmp.getUserId(), LocalDate.now());

        Attendance savedAttendance = attendanceRepo.findByEmployeeUserIdAndDate(loggedInEmp.getUserId(), LocalDate.now())
                .orElseGet(() -> {
                    LocalTime now = LocalTime.now();
                    LocalTime threshold = LocalTime.of(12, 0);

                    boolean isLateArrival = now.isAfter(threshold);

                    Attendance todayAttendance = Attendance.builder()
                            .employee(loggedInEmp)
                            .date(LocalDate.now())
                            .punchInTime(now)
                            .isLate(isLateArrival)
                            .build();

                    log.debug("Creating new attendance row on punch-in. employeeId={}, isLate={}",
                            loggedInEmp.getUserId(), isLateArrival);

                    return attendanceRepo.save(todayAttendance);
                });

        log.info("Punch-in processed. employeeId={}, attendanceId={}, punchInTime={}",
                loggedInEmp.getUserId(), savedAttendance.getAttendanceId(), savedAttendance.getPunchInTime());

        return attendanceMapper.mapToResponse(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceResponse punchOut() {
        Employee loggedInEmp = securityUtil.getLoggedInEmployee();
        log.info("Punch-out requested. employeeId={}, date={}", loggedInEmp.getUserId(), LocalDate.now());

        Attendance todayAttendance = attendanceRepo.findByEmployeeUserIdAndDate(loggedInEmp.getUserId(), LocalDate.now())
                .orElseGet(() -> {
                    log.warn("No attendance row found at punch-out; creating new row. employeeId={}, date={}",
                            loggedInEmp.getUserId(), LocalDate.now());

                    Attendance newTodayAttendance = Attendance.builder()
                            .employee(loggedInEmp)
                            .date(LocalDate.now())
                            .build();
                    return attendanceRepo.save(newTodayAttendance);
                });

        if (todayAttendance.getPunchOutTime() != null) {
            log.info("Punch-out already exists. employeeId={}, attendanceId={}, punchOutTime={}",
                    loggedInEmp.getUserId(), todayAttendance.getAttendanceId(), todayAttendance.getPunchOutTime());
            return attendanceMapper.mapToResponse(todayAttendance);
        }

        todayAttendance.setPunchOutTime(LocalTime.now());

        if (todayAttendance.getPunchInTime() != null) {
            Duration duration = Duration.between(todayAttendance.getPunchInTime(), todayAttendance.getPunchOutTime());
            int totalMinutesWorked = (int) duration.toMinutes();
            todayAttendance.setTotalMinutesWorked(totalMinutesWorked);

            AttendanceStatus todayStatus;
            if (totalMinutesWorked >= 240) {
                todayStatus = AttendanceStatus.PRESENT;
            } else {
                todayStatus = AttendanceStatus.HALF_DAY_PRESENT;
            }
            todayAttendance.setStatus(todayStatus);

            log.debug("Punch-out computed. employeeId={}, attendanceId={}, totalMinutesWorked={}, status={}",
                    loggedInEmp.getUserId(), todayAttendance.getAttendanceId(), totalMinutesWorked, todayStatus);
        } else {
            log.warn("Punch-out without punch-in detected. employeeId={}, attendanceId={}",
                    loggedInEmp.getUserId(), todayAttendance.getAttendanceId());
        }

        log.info("Punch-out processed. employeeId={}, attendanceId={}, punchOutTime={}",
                loggedInEmp.getUserId(), todayAttendance.getAttendanceId(), todayAttendance.getPunchOutTime());

        return attendanceMapper.mapToResponse(todayAttendance);
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(Integer month, Integer year) {
        Employee loggedInEmp = securityUtil.getLoggedInEmployee();
        log.info("Fetching self attendance list. employeeId={}, month={}, year={}",
                loggedInEmp.getUserId(), month, year);

        List<Attendance> attendanceList =
                attendanceRepo.findAttendanceByEmployeeUserIdAndMonthAndYear(loggedInEmp.getUserId(), month, year);

        log.debug("Self attendance fetched. employeeId={}, records={}",
                loggedInEmp.getUserId(), attendanceList.size());

        return attendanceList.stream()
                .map(attendanceMapper::mapToResponse)
                .toList();
    }

    @Override
    public AttendanceResponse getMyAttendanceByDate(LocalDate date) {
        Employee emp = securityUtil.getLoggedInEmployee();
        log.info("Fetching self attendance by date. employeeId={}, date={}", emp.getUserId(), date);

        Attendance todayAttendance = attendanceRepo.findByEmployeeUserIdAndDate(emp.getUserId(), date)
                .orElseThrow(() -> {
                    log.warn("Attendance not found for self. employeeId={}, date={}", emp.getUserId(), date);
                    return new ResourceNotFoundException("Attendance not found on date: " + date);
                });

        return attendanceMapper.mapToResponse(todayAttendance);
    }

    // MANAGER related methods

    @Override
    public Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Integer month, Integer year, AttendanceStatus status, Pageable pageable) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found while fetching attendance. employeeId={}", employeeId);
                    return new ResourceNotFoundException("Employee not found with id: " + employeeId);
                });

        Employee loggedEmployee = securityUtil.getLoggedInEmployee();

        if (emp.getManager() == null || !emp.getManager().getUserId().equals(loggedEmployee.getUserId())) {
            log.warn("Manager access denied for employee attendance. managerId={}, employeeId={}",
                    loggedEmployee.getUserId(), employeeId);
            throw new ForbiddenException("You are not allowed to view attendance for employee id: " + employeeId);
        }

        log.info("Manager fetching employee attendance. managerId={}, employeeId={}, month={}, year={}",
                loggedEmployee.getUserId(), employeeId, month, year);

        Page<Attendance> attendancePage =
                attendanceRepo.findAttendanceByEmployeeUserIdAndMonthAndYear(employeeId, month, year, status, pageable);

        return attendancePage.map(attendanceMapper::mapToResponse);
    }

    @Override
    public Page<AttendanceResponse> getAllAttendanceByDate(LocalDate date, Pageable pageable) {
        Employee loggedInManager = securityUtil.getLoggedInEmployee();
        log.info("Manager fetching team attendance by date. managerId={}, date={}",
                loggedInManager.getUserId(), date);

        Page<Attendance> attendanceByDatePage =
                attendanceRepo.findByEmployee_Manager_UserIdAndDate(loggedInManager.getUserId(), date, pageable);

        return attendanceByDatePage.map(attendanceMapper::mapToResponse);
    }

    @Override
    public MonthlyAttendanceReport getMyMonthlyAttendanceReport(Integer month, Integer year) {
        Employee me = securityUtil.getLoggedInEmployee();
        log.info("Fetching self monthly attendance report. employeeId={}, month={}, year={}",
                me.getUserId(), month, year);
        return fetchMonthlyReport(me.getUserId(), month, year);
    }

    @Override
    public MonthlyAttendanceReport getEmployeeMonthlyAttendanceReport(Long employeeId, Integer month, Integer year) {
        Employee targetEmployee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found while fetching monthly report. employeeId={}", employeeId);
                    return new ResourceNotFoundException("Employee not found with id: " + employeeId);
                });

        Employee loggedInManager = securityUtil.getLoggedInEmployee();

        if (targetEmployee.getManager() == null ||
                !targetEmployee.getManager().getUserId().equals(loggedInManager.getUserId())) {
            log.warn("Manager access denied for monthly report. managerId={}, employeeId={}",
                    loggedInManager.getUserId(), employeeId);
            throw new ForbiddenException("You are not allowed to view monthly report for employee id: " + employeeId);
        }

        log.info("Manager fetching monthly attendance report. managerId={}, employeeId={}, month={}, year={}",
                loggedInManager.getUserId(), employeeId, month, year);

        return fetchMonthlyReport(employeeId, month, year);
    }

    @Override
    public List<TeamAttendanceReportRow> getTeamAttendanceReport(LocalDate date) {
        Employee loggedInManager = securityUtil.getLoggedInEmployee();
        log.info("Manager fetching team daily attendance report. managerId={}, date={}",
                loggedInManager.getUserId(), date);

        List<TeamAttendanceReportRow> rows = attendanceRepo.findTeamAttendanceReportByManagerAndDate(
                loggedInManager.getUserId(),
                date
        );

        log.debug("Team daily attendance report generated. managerId={}, date={}, rowCount={}",
                loggedInManager.getUserId(), date, rows.size());

        return rows;
    }

    private MonthlyAttendanceReport fetchMonthlyReport(Long employeeId, Integer month, Integer year) {
        MonthlyAttendanceReport report =
                attendanceRepo.findMonthlyReportByEmployee_UserIdAndMonthAndYear(employeeId, month, year);

        if (report == null) {
            log.warn("Monthly attendance report not found. employeeId={}, month={}, year={}",
                    employeeId, month, year);
            throw new ResourceNotFoundException(
                    "Monthly attendance report not found for employee id: " + employeeId +
                            ", month: " + month + ", year: " + year
            );
        }

        log.debug("Monthly attendance report fetched. employeeId={}, month={}, year={}",
                employeeId, month, year);

        return report;
    }

    @Transactional
    @Override
    public void markEmployeeAbsent(Employee employee, LocalDate date) {
        Attendance absentRecord = Attendance.builder()
                .employee(employee)
                .date(date)
                .status(AttendanceStatus.ABSENT)
                .build();

        attendanceRepo.save(absentRecord);
    }
}
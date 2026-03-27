package com.ds.app.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.entity.Attendance;
import com.ds.app.entity.Employee;
import com.ds.app.enums.AttendanceStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.AttendanceMapper;
import com.ds.app.repository.IAttendanceRepository;
import com.ds.app.service.IAttendanceService;
import com.ds.app.utils.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService{
	
	private final SecurityUtils securityUtil;
	private final IAttendanceRepository attendanceRepo;
	private final AttendanceMapper attendanceMapper;
	
	// Employee related methods
	
	@Override
	public AttendanceResponse punchIn() {
		Employee loggedInEmp = securityUtil.getLoggedInEmployee();
		
		Attendance todayAttendance = Attendance.builder()
				.employee(loggedInEmp)
				.date(LocalDate.now())
				.punchInTime(LocalTime.now())
				.build();
		
		Attendance savedAttendance = attendanceRepo.save(todayAttendance);
	
		return attendanceMapper.mapToResponse(savedAttendance);
	}

	@Override
	@Transactional
	public AttendanceResponse punchOut() {
		Employee loggedInEmp = securityUtil.getLoggedInEmployee();
		Attendance TodayAttendance = attendanceRepo.findByEmployeeUserIdAndDate(loggedInEmp.getUserId(), LocalDate.now())
				.orElseGet( () -> {
					Attendance newTodayAttendance = Attendance.builder()
							.employee(loggedInEmp)
							.date(LocalDate.now())
							.build();
					return attendanceRepo.save(newTodayAttendance);
				});
		
		TodayAttendance.setPunchOutTime(LocalTime.now());
		
		if(TodayAttendance.getPunchInTime() != null) {
			Duration duration = Duration.between(TodayAttendance.getPunchInTime(), TodayAttendance.getPunchOutTime());
			Double hoursWorked = (double) duration.toHours();
			TodayAttendance.setHoursWorked(hoursWorked);
			
			AttendanceStatus todayStatus;
			if(hoursWorked >= 4) {
				todayStatus = AttendanceStatus.PRESENT;
			}else {
				todayStatus = AttendanceStatus.HALF_DAY_PRESENT;
			}
			TodayAttendance.setStatus(todayStatus);
		}
		
		return attendanceMapper.mapToResponse(TodayAttendance);
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
	
	// HR related methods

	@Override
	public Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Integer month, Integer year, Pageable pageable) {
		Page<Attendance> attendancePage = attendanceRepo.findAttendanceByEmployeeUserIdAndMonthAndYear(employeeId, month, year, pageable);
		return attendancePage.map(attendace -> attendanceMapper.mapToResponse(attendace));
	}

	@Override
	public Page<AttendanceResponse> getAllAttendanceByDate(LocalDate date, Pageable pageable) {
		Page<Attendance> attendanceByDatePage = attendanceRepo.findByDate(date, pageable);
		return attendanceByDatePage.map(attendance -> attendanceMapper.mapToResponse(attendance));
	}

//	@Override
//	public MonthlyAttendanceReport getMonthlyAttendanceReport(Long employeeId, Integer month, Integer year) {
//		return attendanceRepo.findMonthlyReportByEmployeeUserIdAndMonthAndYear(employeeId, month, year);
//		return null;
//	}	
}

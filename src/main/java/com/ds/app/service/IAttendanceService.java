package com.ds.app.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.dto.MonthlyAttendanceReport;

public interface IAttendanceService {
	
	// Employee related methods

	AttendanceResponse punchIn();

	AttendanceResponse punchOut();

	AttendanceResponse getMyAttendanceByDate(LocalDate date);

	List<AttendanceResponse> getMyAttendance(Integer month, Integer year);
	
	// HR related methods

	Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Integer month, Integer year, Pageable pageable);

	Page<AttendanceResponse> getAllAttendanceByDate(LocalDate date, Pageable pageable);

//	MonthlyAttendanceReport getMonthlyAttendanceReport(Long employeeId, Integer month, Integer year);
}

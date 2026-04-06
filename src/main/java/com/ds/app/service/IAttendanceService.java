package com.ds.app.service;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.dto.MonthlyAttendanceReport;
import com.ds.app.dto.TeamAttendanceReportRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IAttendanceService {
	
	// Employee related methods

	AttendanceResponse punchIn();

	AttendanceResponse punchOut();

	AttendanceResponse getMyAttendanceByDate(LocalDate date);

	List<AttendanceResponse> getMyAttendance(Integer month, Integer year);

    MonthlyAttendanceReport getMyMonthlyAttendanceReport(Integer month, Integer year);
	
	// MANAGER related methods

	Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, Integer month, Integer year, Pageable pageable);

	Page<AttendanceResponse> getAllAttendanceByDate(LocalDate date, Pageable pageable);

	MonthlyAttendanceReport getEmployeeMonthlyAttendanceReport(Long employeeId, Integer month, Integer year);

    List<TeamAttendanceReportRow> getTeamAttendanceReport(LocalDate date);
}

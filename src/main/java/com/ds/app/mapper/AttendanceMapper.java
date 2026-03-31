package com.ds.app.mapper;

import com.ds.app.dto.AttendanceResponse;
import com.ds.app.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {
	public AttendanceResponse mapToResponse(Attendance attendance) {
		return AttendanceResponse.builder()
				.attendanceId(attendance.getAttendanceId())
				.employeeId(attendance.getEmployee().getUserId())
				.employeeName(attendance.getEmployee().getUsername())
				.date(attendance.getDate())
				.punchInTime(attendance.getPunchInTime())
				.punchOutTime(attendance.getPunchOutTime())
				.status(attendance.getStatus())
				.hoursWorked(attendance.getHoursWorked())
				.isRegularized(attendance.getIsRegularized())
				.build();
	}
}

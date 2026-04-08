package com.ds.app.mapper;

import com.ds.app.dto.response.AttendanceResponse;
import com.ds.app.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {
	public AttendanceResponse mapToResponse(Attendance attendance) {

        int hours = attendance.getTotalMinutesWorked() != null ? attendance.getTotalMinutesWorked() / 60 : 0;
        int minutes = attendance.getTotalMinutesWorked() != null ? attendance.getTotalMinutesWorked() % 60 : 0;

		return AttendanceResponse.builder()
				.attendanceId(attendance.getAttendanceId())
				.employeeId(attendance.getEmployee().getUserId())
				.employeeName(attendance.getEmployee().getUsername())
				.date(attendance.getDate())
				.punchInTime(attendance.getPunchInTime())
				.punchOutTime(attendance.getPunchOutTime())
				.status(attendance.getStatus())
				.totalMinutesWorked(attendance.getTotalMinutesWorked())
                .formattedTotalHoursWorked(String.format("%02d:%02d", hours, minutes))
                .isLate(attendance.getIsLate())
				.isRegularized(attendance.getIsRegularized())
				.build();
	}
}

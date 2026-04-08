package com.ds.app.dto.response;

import com.ds.app.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamAttendanceReportRow {
    private Long employeeId;
    private String employeeName;
    private LocalDate date;
    private AttendanceStatus status;
    private LocalTime punchInTime;
    private LocalTime punchOutTime;
    private Integer totalMinutesWorked;
    private Boolean isLate;
}
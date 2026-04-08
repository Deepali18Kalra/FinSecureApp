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
public class AttendanceResponse {
    private Long attendanceId;
    private Long employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalTime punchInTime;
    private LocalTime punchOutTime;
    private AttendanceStatus status;
    private Integer totalMinutesWorked;
    private String formattedTotalHoursWorked;
    private boolean isLate;
    private Boolean isRegularized;
}
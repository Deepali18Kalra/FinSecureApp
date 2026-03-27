package com.ds.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAttendanceReport {
	private Long empployeeId;
	private String employeeName;
	private Integer month;
	private Integer year;
	private Integer presentCount;
	private Integer absentCount;
	private Integer lateCount;
	private Integer halfDayCount;
	private Double totalMonthlyHours;
}

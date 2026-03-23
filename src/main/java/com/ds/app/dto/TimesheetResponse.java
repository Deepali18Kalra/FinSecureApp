package com.ds.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ds.app.entity.TimesheetStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetResponse {
	private Long timesheetId;
	private Long employeeId;
	private String employeeName;
	private Integer month;
	private Integer year;
	private TimesheetStatus status;
	private LocalDateTime submittedAt;
	private List<TimesheetEntryResponse> entries;
}

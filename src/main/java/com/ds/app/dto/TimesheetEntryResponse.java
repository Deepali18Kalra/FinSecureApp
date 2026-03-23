package com.ds.app.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetEntryResponse {
	private Long entryId;
	private LocalDate date;
	private String taskDesdription;
	private Double hoursWorked;
}

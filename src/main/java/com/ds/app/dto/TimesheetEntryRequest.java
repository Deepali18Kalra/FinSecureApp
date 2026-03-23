package com.ds.app.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetEntryRequest {
	
	@NotNull(message = "Date is required")
	private LocalDate date;
	
	@NotBlank(message = "Task description is required")
	private String taskDescription;
	
	@NotNull(message = "Hours worked is required")
	@Min(value = 1, message = "Hours worked must be greater than 0")
	private Double hoursWorked;
}

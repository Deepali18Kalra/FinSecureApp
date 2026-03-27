package com.ds.app.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
	@PastOrPresent(message = "Cannot make entry for future dates")
	private LocalDate date;
	
	@NotBlank(message = "Task description is required")
	private String taskDescription;
	
	@NotNull(message = "Hours worked is required")
	@Positive(message = "Hours worked must be greater than 0")
	@DecimalMax(value = "24.0", message = "Hours worked cannot exeed 24")
	private Double hoursWorked;
	
	private Long projectId;
	
	@NotBlank(message = "Project name is required")
	private String projectName;
}

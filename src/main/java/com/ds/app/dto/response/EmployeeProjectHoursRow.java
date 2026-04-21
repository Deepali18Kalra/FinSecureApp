package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProjectHoursRow {
	 private Long employeeId;
	 private String employeeName;
	 private double hoursWorked;
}

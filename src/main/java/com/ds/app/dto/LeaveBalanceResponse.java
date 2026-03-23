package com.ds.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {
	private Long employeeId;
	private String employeeName;
	private Integer year;
	private Integer sickLeaveBalance;
	private Integer casualLeaveBalance;
	private Integer earnedLeaveBalace;
}

package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {

    private Long employeeId;
    private String employeeName;
    private Integer year;

    // sick leave
    private Integer sickLeaveBalance;
    private Integer reservedSickLeaves;
    private Integer sickLeavesConsumed;

    // casual leave
    private Integer casualLeaveBalance;
    private Integer reservedCasualLeaves;
    private Integer casualLeavesConsumed;

    // earned leave
    private BigDecimal earnedLeaveBalance;
    private Integer reservedEarnedLeaves;
    private Integer earnedLeavesConsumed;
    private BigDecimal carriedForwardEarnedDays;
}
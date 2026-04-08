package com.ds.app.mapper;

import com.ds.app.dto.response.LeaveBalanceResponse;
import com.ds.app.entity.LeaveBalance;
import org.springframework.stereotype.Component;

@Component
public class LeaveBalanceMapper {

    public LeaveBalanceResponse mapToResponse(LeaveBalance lb) {
        String employeeName = lb.getEmployee().getFirstName()
                + " " + lb.getEmployee().getLastName();

        return LeaveBalanceResponse.builder()
                .employeeId(lb.getEmployee().getUserId())
                .employeeName(employeeName)
                .year(lb.getYear())
                // sick
                .sickLeaveBalance(lb.getSickLeaveBalance())
                .reservedSickLeaves(lb.getReservedSickLeaves())
                .sickLeavesConsumed(lb.getSickLeavesConsumed())
                // casual
                .casualLeaveBalance(lb.getCasualLeaveBalance())
                .reservedCasualLeaves(lb.getReservedCasualLeaves())
                .casualLeavesConsumed(lb.getCasualLeavesConsumed())
                // earned
                .earnedLeaveBalance(lb.getEarnedLeaveBalance())
                .reservedEarnedLeaves(lb.getReservedEarnedLeaves())
                .earnedLeavesConsumed(lb.getEarnedLeavesConsumed())
                .carriedForwardEarnedDays(lb.getCarriedForwardEarnedDays())
                .build();
    }
}
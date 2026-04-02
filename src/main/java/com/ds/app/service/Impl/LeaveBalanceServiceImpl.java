package com.ds.app.service.Impl;

import com.ds.app.entity.LeaveBalance;
import com.ds.app.enums.LeaveType;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.ILeaveBalanceRepository;
import com.ds.app.service.ILeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements ILeaveBalanceService {

    private final ILeaveBalanceRepository leaveBalanceRepository;

    @Override
    public void reserveLeaves(Long userId, int year, LeaveType type, int days) {
        if (type == LeaveType.UNPAID) return;

        LeaveBalance lb = findByEmployeeAndYear(userId, year);

        switch (type) {
            case SICK -> {
                int available = lb.getSickLeaveBalance() - lb.getReservedSickLeaves();
                if (available < days) throw new IllegalArgumentException("Insufficient sick leave balance");
                lb.setReservedSickLeaves(lb.getReservedSickLeaves() + days);
            }
            case CASUAL -> {
                int available = lb.getCasualLeaveBalance() - lb.getReservedCasualLeaves();
                if (available < days) throw new IllegalArgumentException("Insufficient casual leave balance");
                lb.setReservedCasualLeaves(lb.getReservedCasualLeaves() + days);
            }
            case EARNED -> {
                int available = lb.getEarnedLeaveBalance().intValue() - lb.getReservedEarnedLeaves();
                if (available < days) throw new IllegalArgumentException("Insufficient earned leave balance");
                lb.setReservedEarnedLeaves(lb.getReservedEarnedLeaves() + days);
            }
            default -> { }
        }
    }

    @Override
    public void releaseReservedLeaves(Long userId, int year, LeaveType type, int days) {
        if (type == LeaveType.UNPAID) return;

        LeaveBalance lb = findByEmployeeAndYear(userId, year);

        switch (type) {
            case SICK -> lb.setReservedSickLeaves(Math.max(0, lb.getReservedSickLeaves() - days));
            case CASUAL -> lb.setReservedCasualLeaves(Math.max(0, lb.getReservedCasualLeaves() - days));
            case EARNED -> lb.setReservedEarnedLeaves(Math.max(0, lb.getReservedEarnedLeaves() - days));
            default -> { }
        }
    }

    @Override
    public void applyApproval(Long userId, int year, LeaveType type, int days) {
        if (type == LeaveType.UNPAID) return;

        LeaveBalance lb = findByEmployeeAndYear(userId, year);

        switch (type) {
            case SICK -> {
                if (lb.getReservedSickLeaves() < days) {
                    throw new IllegalStateException("Reserved sick leaves less than requested days");
                }
                lb.setReservedSickLeaves(lb.getReservedSickLeaves() - days);
                lb.setSickLeaveBalance(lb.getSickLeaveBalance() - days);
                lb.setSickLeavesConsumed(lb.getSickLeavesConsumed() + days);
            }
            case CASUAL -> {
                if (lb.getReservedCasualLeaves() < days) {
                    throw new IllegalStateException("Reserved casual leaves less than requested days");
                }
                lb.setReservedCasualLeaves(lb.getReservedCasualLeaves() - days);
                lb.setCasualLeaveBalance(lb.getCasualLeaveBalance() - days);
                lb.setCasualLeavesConsumed(lb.getCasualLeavesConsumed() + days);
            }
            case EARNED -> {
                if (lb.getReservedEarnedLeaves() < days) {
                    throw new IllegalStateException("Reserved earned leaves less than requested days");
                }
                lb.setReservedEarnedLeaves(lb.getReservedEarnedLeaves() - days);
                lb.setEarnedLeaveBalance(lb.getEarnedLeaveBalance().subtract(BigDecimal.valueOf(days)));
                lb.setEarnedLeavesConsumed(lb.getEarnedLeavesConsumed() + days);
            }
            default -> { }
        }
    }

    @Override
    public void applyCancellationApproval(Long userId, int year, LeaveType type, int days) {
        if (type == LeaveType.UNPAID) return;

        LeaveBalance lb = findByEmployeeAndYear(userId, year);

        switch (type) {
            case SICK -> {
                lb.setSickLeaveBalance(lb.getSickLeaveBalance() + days);
                lb.setSickLeavesConsumed(lb.getSickLeavesConsumed() - days);
            }
            case CASUAL -> {
                lb.setCasualLeaveBalance(lb.getCasualLeaveBalance() + days);
                lb.setCasualLeavesConsumed(lb.getCasualLeavesConsumed() - days);
            }
            case EARNED -> {
                lb.setEarnedLeaveBalance(lb.getEarnedLeaveBalance().add(BigDecimal.valueOf(days)));
                lb.setEarnedLeavesConsumed(lb.getEarnedLeavesConsumed() - days);
            }
            default -> { }
        }
    }

    private LeaveBalance findByEmployeeAndYear(Long userId, int year) {
        return leaveBalanceRepository.findByEmployeeUserIdAndYear(userId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee/year"));
    }
}
package com.ds.app.service.Impl;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.LeaveRequest;
import com.ds.app.dto.LeaveResponse;
import com.ds.app.dto.LeaveStatusResponse;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import com.ds.app.entity.LeaveBalance;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.enums.LeaveType;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.exception.UnAuthorizedException;
import com.ds.app.mapper.LeaveMapper;
import com.ds.app.repository.IHolidayRepository;
import com.ds.app.repository.ILeaveBalanceRepository;
import com.ds.app.repository.ILeaveRepository;
import com.ds.app.service.ILeaveService;
import com.ds.app.utils.DateUtil;
import com.ds.app.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements ILeaveService {

    private final ILeaveRepository leaveRepository;
    private final IHolidayRepository holidayRepository;
    private final LeaveMapper leaveMapper;
    private final SecurityUtils securityUtils;
    private final ILeaveBalanceRepository leaveBalanceRepository;

    // Employee related methods
    @Override
    @Transactional
    public LeaveResponse applyLeave(LeaveRequest leaveRequest) {
        Employee emp = securityUtils.getLoggedInEmployee();

        LocalDate startDate = leaveRequest.getStartDate();
        LocalDate endDate = leaveRequest.getEndDate();

        Set<LocalDate> holidays = holidayRepository.findDatesBetween(startDate, endDate);
        int workingDays = DateUtil.workingDaysExcludingHolidays(startDate, endDate, holidays);

        int leaveYear = startDate.getYear();

        LeaveBalance lb = leaveBalanceRepository
                .findByEmployeeUserIdAndYear(emp.getUserId(), leaveYear)
                .orElseThrow(() -> new IllegalStateException("Leave balance not found for year: " + leaveYear));

        LeaveType type = leaveRequest.getLeaveType();
        if (type != LeaveType.UNPAID) {
            switch (type) {
                case SICK -> {
                    int available = lb.getSickLeaveBalance() - lb.getReservedSickLeaves();
                    if (available < workingDays) throw new IllegalArgumentException("Insufficient sick leave balance");
                    lb.setReservedSickLeaves(lb.getReservedSickLeaves() + workingDays);
                }
                case CASUAL -> {
                    int available = lb.getCasualLeaveBalance() - lb.getReservedCasualLeaves();
                    if (available < workingDays) throw new IllegalArgumentException("Insufficient casual leave balance");
                    lb.setReservedCasualLeaves(lb.getReservedCasualLeaves() + workingDays);
                }
                case EARNED -> {
                    int available = lb.getEarnedLeaveBalance().intValue() - lb.getReservedEarnedLeaves();
                    if (available < workingDays) throw new IllegalArgumentException("Insufficient earned leave balance");
                    lb.setReservedEarnedLeaves(lb.getReservedEarnedLeaves() + workingDays);
                }
                default -> {}
            }
        }

        Leave leave = leaveMapper.mapToEntity(leaveRequest, emp);
        leave.setTotalDays(workingDays);
        leave.setStatus(LeaveStatus.PENDING);

        Leave saved = leaveRepository.save(leave);
        return leaveMapper.mapToResponse(saved);
    }

    @Override
    public Page<LeaveStatusResponse> getMyLeaves(LeaveStatus status, Integer year, Integer month, Pageable pageable) {
        Employee employee = securityUtils.getLoggedInEmployee();
        return leaveRepository.searchLeaveByEmployee(employee.getUserId(),status,year,month,pageable);
    }

    @Override
    @Transactional
    public LeaveResponse cancelOrWithdrawLeave(Long leaveId) {
        Employee employee = securityUtils.getLoggedInEmployee();

        Leave existingLeave = leaveRepository.findByLeaveIdAndEmployeeUserId(leaveId, employee.getUserId())
                .orElseThrow(() -> new UnAuthorizedException(
                        "Leave not found or you are unauthorized to perform this action"));

        LeaveStatus currentStatus = existingLeave.getStatus();

        // Case 1: Pending leave -> direct withdraw
        if (currentStatus == LeaveStatus.PENDING) {
            existingLeave.setStatus(LeaveStatus.WITHDRAWN);

            // release reserved balance for paid leaves
            if (existingLeave.getLeaveType() != LeaveType.UNPAID) {
                int year = existingLeave.getStartDate().getYear();
                int days = existingLeave.getTotalDays();

                LeaveBalance lb = leaveBalanceRepository
                        .findByEmployeeUserIdAndYear(employee.getUserId(), year)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee/year"));

                switch (existingLeave.getLeaveType()) {
                    case SICK -> lb.setReservedSickLeaves(Math.max(0, lb.getReservedSickLeaves() - days));
                    case CASUAL -> lb.setReservedCasualLeaves(Math.max(0, lb.getReservedCasualLeaves() - days));
                    case EARNED -> lb.setReservedEarnedLeaves(Math.max(0, lb.getReservedEarnedLeaves() - days));
                    default -> {}
                }
            }

            return leaveMapper.mapToResponse(existingLeave);
        }

        // Case 2: Approved leave -> request cancellation, HR must act
        if (currentStatus == LeaveStatus.APPROVED) {
            existingLeave.setStatus(LeaveStatus.CANCELLATION_PENDING);
            return leaveMapper.mapToResponse(existingLeave);
        }

        throw new IllegalStateException("Leave can only be withdrawn when PENDING or cancellation-requested when APPROVED");
    }
    
    // HR related methods
    @Override
    @Transactional
    public LeaveResponse processLeaveRequest(Long leaveId, ApprovalRequest approvalRequest) {
        Employee loggedInHR = securityUtils.getLoggedInEmployee();

        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (!existingLeave.getStatus().equals(LeaveStatus.PENDING)) {
            throw new IllegalStateException("Only pending leaves can be processed");
        }

        LeaveStatus newStatus = toLeaveStatus(approvalRequest.getStatus());
        existingLeave.setStatus(newStatus);
        existingLeave.setApprovedBy(loggedInHR);
        existingLeave.setApprovalDate(LocalDate.now());

        if (approvalRequest.getStatus().equals(ApprovalStatus.REJECTED)) {
            existingLeave.setRejectionReason(approvalRequest.getRejectionReason());
        }

        LeaveType leaveType = existingLeave.getLeaveType();
        int days = existingLeave.getTotalDays();

        if (leaveType != LeaveType.UNPAID) {
            int year = existingLeave.getStartDate().getYear();
            Long empUserId = existingLeave.getEmployee().getUserId();

            LeaveBalance lb = leaveBalanceRepository.findByEmployeeUserIdAndYear(empUserId, year)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee/year"));

            if (LeaveStatus.APPROVED.equals(newStatus)) {
                switch (leaveType) {
                    case SICK -> {
                        if (lb.getReservedSickLeaves() < days) throw new IllegalStateException("Reserved sick leaves less than requested days");
                        lb.setReservedSickLeaves(lb.getReservedSickLeaves() - days);
                        lb.setSickLeaveBalance(lb.getSickLeaveBalance() - days);
                        lb.setSickLeavesConsumed(lb.getSickLeavesConsumed() + days);
                    }
                    case CASUAL -> {
                        if (lb.getReservedCasualLeaves() < days) throw new IllegalStateException("Reserved casual leaves less than requested days");
                        lb.setReservedCasualLeaves(lb.getReservedCasualLeaves() - days);
                        lb.setCasualLeaveBalance(lb.getCasualLeaveBalance() - days);
                        lb.setCasualLeavesConsumed(lb.getCasualLeavesConsumed() + days);
                    }
                    case EARNED -> {
                        if (lb.getReservedEarnedLeaves() < days) throw new IllegalStateException("Reserved earned leaves less than requested days");
                        lb.setReservedEarnedLeaves(lb.getReservedEarnedLeaves() - days);
                        lb.setEarnedLeaveBalance(lb.getEarnedLeaveBalance().subtract(BigDecimal.valueOf(days)));
                        lb.setEarnedLeavesConsumed(lb.getEarnedLeavesConsumed() + days);
                    }
                    default -> {}
                }
            } else if (LeaveStatus.REJECTED.equals(newStatus)) {
                switch (leaveType) {
                    case SICK -> lb.setReservedSickLeaves(Math.max(0, lb.getReservedSickLeaves() - days));
                    case CASUAL -> lb.setReservedCasualLeaves(Math.max(0, lb.getReservedCasualLeaves() - days));
                    case EARNED -> lb.setReservedEarnedLeaves(Math.max(0, lb.getReservedEarnedLeaves() - days));
                    default -> {}
                }
            }
        }

        return leaveMapper.mapToResponse(existingLeave);
    }

    @Override
    @Transactional
    public LeaveResponse processCancellationRequest(Long leaveId, ApprovalRequest approvalRequest) {
        Employee loggedInHR = securityUtils.getLoggedInEmployee();

        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (!LeaveStatus.CANCELLATION_PENDING.equals(existingLeave.getStatus())) {
            throw new IllegalStateException("Only cancellation pending leaves can be processed");
        }

        // HR metadata
        existingLeave.setApprovedBy(loggedInHR);
        existingLeave.setApprovalDate(LocalDate.now());

        LeaveType leaveType = existingLeave.getLeaveType();
        int days = existingLeave.getTotalDays();

        if (ApprovalStatus.APPROVED.equals(approvalRequest.getStatus())) {
            // cancellation approved -> leave cancelled
            existingLeave.setStatus(LeaveStatus.CANCELLED);
            existingLeave.setRejectionReason(null);

            // restore balances for paid leaves (because previously approved leave had deducted them)
            if (leaveType != LeaveType.UNPAID) {
                int year = existingLeave.getStartDate().getYear();
                Long empUserId = existingLeave.getEmployee().getUserId();

                LeaveBalance lb = leaveBalanceRepository.findByEmployeeUserIdAndYear(empUserId, year)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for employee/year"));

                switch (leaveType) {
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
                    default -> {}
                }
            }

        } else if (ApprovalStatus.REJECTED.equals(approvalRequest.getStatus())) {
            // cancellation rejected -> original leave remains approved
            existingLeave.setStatus(LeaveStatus.APPROVED);
            existingLeave.setRejectionReason(approvalRequest.getRejectionReason());
        } else {
            throw new IllegalArgumentException("Unsupported approval status for cancellation request");
        }

        return leaveMapper.mapToResponse(existingLeave);
    }

    @Override
    public Page<LeaveResponse> getPendingRequest(Pageable pageable) {
        Employee loggedInHR = securityUtils.getLoggedInEmployee();
        Page<Leave> pendingLeaves = leaveRepository.findByEmployee_Hr_UserIdAndStatus(
                loggedInHR.getUserId(),
                LeaveStatus.PENDING,
                pageable);
        return pendingLeaves.map(leave -> leaveMapper.mapToResponse(leave));
    }

    // Helper methods
    private LeaveStatus toLeaveStatus(ApprovalStatus approvalStatus) {
        return switch (approvalStatus) {
            case APPROVED -> LeaveStatus.APPROVED;
            case REJECTED -> LeaveStatus.REJECTED;
        };
    }
}
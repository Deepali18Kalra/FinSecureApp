package com.ds.app.service.Impl;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.LeaveRequest;
import com.ds.app.dto.response.LeaveResponse;
import com.ds.app.dto.response.LeaveStatusResponse;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.enums.LeaveType;
import com.ds.app.exception.ForbiddenException;
import com.ds.app.exception.InvalidLeaveStateException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.LeaveMapper;
import com.ds.app.repository.IHolidayRepository;
import com.ds.app.repository.ILeaveRepository;
import com.ds.app.service.IEmailService;
import com.ds.app.service.ILeaveBalanceService;
import com.ds.app.service.ILeaveService;
import com.ds.app.utils.DateUtil;
import com.ds.app.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements ILeaveService {

    private final ILeaveRepository leaveRepository;
    private final IHolidayRepository holidayRepository;
    private final LeaveMapper leaveMapper;
    private final SecurityUtils securityUtils;
    private final ILeaveBalanceService leaveBalanceService;
    private final IEmailService emailService;

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
        LeaveType type = leaveRequest.getLeaveType();

        leaveBalanceService.reserveLeaves(emp.getUserId(), leaveYear, type, workingDays);

        Leave leave = leaveMapper.mapToEntity(leaveRequest, emp);
        leave.setTotalDays(workingDays);
        leave.setStatus(LeaveStatus.PENDING);

        Leave saved = leaveRepository.save(leave);

        emailService.notifyManagerForNewLeave(emp, saved);

        return leaveMapper.mapToResponse(saved);
    }

    @Override
    public Page<LeaveStatusResponse> getMyLeaves(LeaveStatus status, Integer year, Integer month, Pageable pageable) {
        Employee employee = securityUtils.getLoggedInEmployee();
        return leaveRepository.searchLeaveByEmployee(employee.getUserId(), status, year, month, pageable);
    }

    @Override
    @Transactional
    public LeaveResponse cancelOrWithdrawLeave(Long leaveId) {
        Employee employee = securityUtils.getLoggedInEmployee();

        Leave existingLeave = leaveRepository.findByLeaveIdAndEmployeeUserId(leaveId, employee.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        LeaveStatus currentStatus = existingLeave.getStatus();

        if (currentStatus == LeaveStatus.PENDING) {
            existingLeave.setStatus(LeaveStatus.WITHDRAWN);

            leaveBalanceService.releaseReservedLeaves(
                    employee.getUserId(),
                    existingLeave.getStartDate().getYear(),
                    existingLeave.getLeaveType(),
                    existingLeave.getTotalDays()
            );

            return leaveMapper.mapToResponse(existingLeave);
        }

        if (currentStatus == LeaveStatus.APPROVED) {
            existingLeave.setStatus(LeaveStatus.CANCELLATION_PENDING);

            emailService.notifyManagerForCancellationRequest(employee, existingLeave);

            return leaveMapper.mapToResponse(existingLeave);
        }

        throw new InvalidLeaveStateException("Leave can only be withdrawn when PENDING or cancelled when APPROVED");
    }

    // MANAGER related methods
    @Override
    @Transactional
    public LeaveResponse processLeaveRequest(Long leaveId, ApprovalRequest approvalRequest) {
        Employee loggedInManager = securityUtils.getLoggedInEmployee();

        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (existingLeave.getEmployee().getManager() == null ||
                !existingLeave.getEmployee().getManager().getUserId()
                        .equals(loggedInManager.getUserId())) {
            throw new ForbiddenException("You are not authorized to process this leave");
        }

        if (!existingLeave.getStatus().equals(LeaveStatus.PENDING)) {
            throw new InvalidLeaveStateException("Only PENDING leaves can be processed");
        }

        LeaveStatus newStatus = toLeaveStatus(approvalRequest.getStatus());
        existingLeave.setStatus(newStatus);
        existingLeave.setApprovedBy(loggedInManager);
        existingLeave.setApprovalDate(LocalDate.now());

        if (approvalRequest.getStatus().equals(ApprovalStatus.REJECTED)) {
            existingLeave.setRejectionReason(approvalRequest.getRejectionReason());
        }

        LeaveType leaveType = existingLeave.getLeaveType();
        int days = existingLeave.getTotalDays();
        int year = existingLeave.getStartDate().getYear();
        Long empUserId = existingLeave.getEmployee().getUserId();

        if (LeaveStatus.APPROVED.equals(newStatus)) {
            leaveBalanceService.applyApproval(empUserId, year, leaveType, days);
        } else if (LeaveStatus.REJECTED.equals(newStatus)) {
            leaveBalanceService.releaseReservedLeaves(empUserId, year, leaveType, days);
        }

        emailService.notifyEmployeeForLeaveDecision(existingLeave.getEmployee(), existingLeave);

        return leaveMapper.mapToResponse(existingLeave);
    }

    @Override
    @Transactional
    public LeaveResponse processCancellationRequest(Long leaveId, ApprovalRequest approvalRequest) {
        Employee loggedInManager = securityUtils.getLoggedInEmployee();

        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if (existingLeave.getEmployee().getManager() == null ||
                !existingLeave.getEmployee().getManager().getUserId()
                        .equals(loggedInManager.getUserId())) {
            throw new ForbiddenException("You are not authorized to process this leave");
        }

        if (!LeaveStatus.CANCELLATION_PENDING.equals(existingLeave.getStatus())) {
            throw new InvalidLeaveStateException("Only CANCELLATION_PENDING leaves can be processed");
        }

        existingLeave.setApprovedBy(loggedInManager);
        existingLeave.setApprovalDate(LocalDate.now());

        LeaveType leaveType = existingLeave.getLeaveType();
        int days = existingLeave.getTotalDays();
        int year = existingLeave.getStartDate().getYear();
        Long empUserId = existingLeave.getEmployee().getUserId();

        if (ApprovalStatus.APPROVED.equals(approvalRequest.getStatus())) {
            existingLeave.setStatus(LeaveStatus.CANCELLED);
            existingLeave.setRejectionReason(null);

            leaveBalanceService.applyCancellationApproval(empUserId, year, leaveType, days);

        } else if (ApprovalStatus.REJECTED.equals(approvalRequest.getStatus())) {
            existingLeave.setStatus(LeaveStatus.APPROVED);
            existingLeave.setRejectionReason(approvalRequest.getRejectionReason());
        } else {
            throw new IllegalArgumentException("Unsupported approval status for cancellation request");
        }

        emailService.notifyEmployeeForCancellationDecision(
                existingLeave.getEmployee(),
                existingLeave,
                approvalRequest.getStatus(),
                approvalRequest.getRejectionReason()
        );

        return leaveMapper.mapToResponse(existingLeave);
    }

    @Override
    public Page<LeaveResponse> getPendingRequest(Pageable pageable) {
        Employee loggedInManager = securityUtils.getLoggedInEmployee();
        List<LeaveStatus> status = List.of(LeaveStatus.PENDING, LeaveStatus.CANCELLATION_PENDING);
        Page<Leave> pendingLeaves = leaveRepository.findByEmployee_Manager_UserIdAndStatusIn(
                loggedInManager.getUserId(),
                status,
                pageable);
        return pendingLeaves.map(leaveMapper::mapToResponse);
    }

    // Helper methods
    private LeaveStatus toLeaveStatus(ApprovalStatus approvalStatus) {
        return switch (approvalStatus) {
            case APPROVED -> LeaveStatus.APPROVED;
            case REJECTED -> LeaveStatus.REJECTED;
        };
    }
}
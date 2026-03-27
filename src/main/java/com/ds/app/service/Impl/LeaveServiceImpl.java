package com.ds.app.service.Impl;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.LeaveRequest;
import com.ds.app.dto.LeaveResponse;
import com.ds.app.dto.LeaveStatusResponse;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.LeaveMapper;
import com.ds.app.repository.IHolidayRepository;
import com.ds.app.repository.ILeaveRepository;
import com.ds.app.service.ILeaveService;
import com.ds.app.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements ILeaveService {

    private final ILeaveRepository leaveRepository;
    private final IHolidayRepository holidayRepository;
    private final LeaveMapper leaveMapper;
    private final SecurityUtils securityUtils;

    // Employee related methods
    @Override
    public LeaveResponse applyLeave(LeaveRequest leaveRequest) {
        Employee loggedInEmployee = securityUtils.getLoggedInEmployee();
        Leave leave = leaveMapper.mapToEntity(leaveRequest, loggedInEmployee);

        LocalDate startDate = leaveRequest.getStartDate();
        LocalDate endDate = leaveRequest.getEndDate();

        Set<LocalDate> holidays = holidayRepository.findDatesBetween(startDate, endDate);
        int workingDays = com.ds.app.utils.DateUtil.workingDaysExcludingHolidays(startDate, endDate, holidays);

        leave.setTotalDays(workingDays);

        Leave savedLeave = leaveRepository.save(leave);
        return leaveMapper.mapToResponse(savedLeave);
    }

    @Override
    public LeaveStatusResponse getLeaveStatus(Long leaveId) {
        return leaveRepository.findByLeaveId(leaveId);
    }

    // HR related methods
    @Override
    @Transactional
    public LeaveResponse reviewLeave(Long leaveId, ApprovalRequest approvalRequest) {
        Employee loggedInHR = securityUtils.getLoggedInEmployee();
        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        if(existingLeave.getStatus().equals(LeaveStatus.REJECTED)) {
            existingLeave.setStatus(toLeaveStatus(approvalRequest.getStatus()));
            existingLeave.setApprovedBy(loggedInHR);
            existingLeave.setApprovalDate(LocalDate.now());
        }

        if(approvalRequest.getStatus().equals(ApprovalStatus.REJECTED)) {
            existingLeave.setReason(approvalRequest.getRejectionReason());
        }

        return  leaveMapper.mapToResponse(existingLeave);
    }

    @Override
    public Page<LeaveResponse> getPendingRequest(Pageable pageable) {
        Employee loggedInHR = securityUtils.getLoggedInEmployee();
        Page<Leave> pendingLeaves = leaveRepository.findByEmployeeHrUserId(loggedInHR.getUserId(), pageable);
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
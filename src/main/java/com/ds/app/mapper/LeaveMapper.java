package com.ds.app.mapper;

import com.ds.app.dto.request.LeaveRequest;
import com.ds.app.dto.response.LeaveResponse;
import com.ds.app.dto.response.LeaveStatusResponse;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Leave;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    public Leave mapToEntity(LeaveRequest leaveRequest, Employee employee) {
        return Leave.builder()
                .employee(employee)
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .leaveType(leaveRequest.getLeaveType())
                .reasonForLeave(leaveRequest.getReasonForLeave())
                .build();
    }

    public LeaveResponse mapToResponse(Leave leave) {
        return LeaveResponse.builder()
                .leaveId(leave.getLeaveId())
                .employeeId(leave.getEmployee().getUserId())
                .employeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .leaveType(leave.getLeaveType())
                .reasonForLeave(leave.getReasonForLeave())
                .status(leave.getStatus())
                .approvedByName(leave.getApprovedBy() != null ?
                    leave.getApprovedBy().getFirstName() + " " + leave.getApprovedBy().getLastName() :
                    null)
                .approvalDate(leave.getApprovalDate())
                .rejectionReason(leave.getRejectionReason())
                .build();
    }

    public LeaveStatusResponse mapToStatusResponse(Leave leave) {
        return LeaveStatusResponse.builder()
                .leaveId(leave.getLeaveId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .leaveType(leave.getLeaveType())
                .status(leave.getStatus())
                .approvalDate(leave.getApprovalDate())
                .rejectionReason(leave.getRejectionReason())
                .build();
    }
}
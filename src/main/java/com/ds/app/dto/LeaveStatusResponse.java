package com.ds.app.dto;

import com.ds.app.enums.LeaveStatus;
import com.ds.app.enums.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveStatusResponse {
    private Long leaveId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private LeaveStatus status;
    private LocalDate approvalDate;
    private String rejectionReason;
}
package com.ds.app.dto.response;

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
public class LeaveResponse {
    private Long leaveId;
    private Long employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private LeaveType leaveType;
    private String reasonForLeave;
    private LeaveStatus status;
    private String approvedByName;
    private LocalDate approvalDate;
    private String rejectionReason;
}
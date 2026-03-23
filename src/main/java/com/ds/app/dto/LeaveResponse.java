package com.ds.app.dto;

import com.ds.app.entity.LeaveStatus;
import com.ds.app.entity.LeaveType;
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
    private String reason;
    private LeaveStatus status;
    private String approvedByName;
}
package com.ds.app.dto.response;

import com.ds.app.enums.RegularizationRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegularizationResponse {
    private Long requestId;
    private Long employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalTime punchInTime;
    private LocalTime punchOutTime;
    private String reason;
    private RegularizationRequestStatus status;
    private String approvedByName;
    private String rejectionReason;
}
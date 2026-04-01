package com.ds.app.dto.response;

import com.ds.app.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryJobResponseDTO {

    private Long id;
    private String jobName;
    private LocalDateTime scheduledDateTime;
    private String targetMonth;        // "2025-03" format
    private JobStatus jobStatus;
    private Integer totalEmployees;
    private Integer successCount;
    private Integer failureCount;
    private Long createdBy;      // resolved from User
    private LocalDateTime createdAt;
}

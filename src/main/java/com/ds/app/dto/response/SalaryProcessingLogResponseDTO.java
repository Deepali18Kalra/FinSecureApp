package com.ds.app.dto.response;

import com.ds.app.enums.SalaryProcessingStatus;
import com.ds.app.enums.SalarySkipReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryProcessingLogResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String salaryMonth;
    private SalaryProcessingStatus processingStatus;
    private SalarySkipReason skipReason;
    private String details;
    private LocalDateTime loggedAt;
    
}
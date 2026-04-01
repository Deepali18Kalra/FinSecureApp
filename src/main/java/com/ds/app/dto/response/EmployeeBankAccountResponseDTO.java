package com.ds.app.dto.response;

import com.ds.app.enums.BankValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeBankAccountResponseDTO {

    private Long empBankId;
    private Long employeeId;
    private String bankName;  
    private String bankCode;// resolved from BankMaster
    private String ifscCode;
    private String accountNumberMasked; // e.g. ****1234  — masked by system
    private String accountHolderName;
    private String reviewNote;
    private Long reviewedBy; // HR/Admin user ID who reviewed this account
    private BankValidationStatus validationStatus;
    private LocalDateTime createdAt;
}

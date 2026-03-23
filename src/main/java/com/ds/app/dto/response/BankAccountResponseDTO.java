package com.ds.app.dto.response;

import com.ds.app.enums.BankStatus;
import com.ds.app.enums.BankValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountResponseDTO {

    private Long id;
    private Long employeeId;
    private String bankName;           // resolved from BankMaster
    private String ifscCode;
    private String accountNumberMasked; // e.g. ****1234  — masked by system
    private String accountHolderName;
    private Boolean isPrimary;
    private BankValidationStatus validationStatus;
    private LocalDateTime createdAt;
}

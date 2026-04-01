package com.ds.app.dto.response;

import com.ds.app.enums.BankStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinanceBankAccountResponseDTO {

    private Long bankId;
    private String bankName;
    private String bankCode;
    private BankStatus status;
    private Long addedBy;      // resolved from User — name only, not ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

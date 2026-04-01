package com.ds.app.dto.response;

import com.ds.app.enums.FundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceInvestmentResponseDTO {

    private Long mutualFundId;
    private String fundName;
    private String fundCode;
    private String category;
    private FundStatus status;
    private Long addedBy;    // resolved from User
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

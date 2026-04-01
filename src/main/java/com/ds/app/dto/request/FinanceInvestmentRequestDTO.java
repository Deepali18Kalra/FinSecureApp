package com.ds.app.dto.request;

import com.ds.app.enums.FundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceInvestmentRequestDTO {

    @NotBlank(message = "Fund name is required")
    @Size(max = 150)
    private String fundName;

    @NotBlank(message = "Fund code is required")
    @Size(max = 30)
    private String fundCode;

    @NotBlank(message = "Category is required")
    @Size(max = 80)
    private String category;  // e.g. Equity, Debt, Hybrid

    @NotNull(message = "Status is required")
    private FundStatus status;  // WHITELISTED or BLACKLISTED
}

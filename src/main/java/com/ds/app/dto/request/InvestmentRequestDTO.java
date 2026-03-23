package com.ds.app.dto.request;

import com.ds.app.enums.InvestmentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequestDTO {

    @NotNull(message = "Investment type is required")
    private InvestmentType investmentType;

    // Required only when investmentType = MUTUAL_FUND
    private Long fundId;

    // Required when investmentType = DIRECT_EQUITY or BONDS
    private String securityName;

    @NotNull(message = "Declared amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal declaredAmount;
}

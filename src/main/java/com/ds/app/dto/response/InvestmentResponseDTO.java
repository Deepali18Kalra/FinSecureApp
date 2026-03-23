package com.ds.app.dto.response;

import com.ds.app.enums.ComplianceStatus;
import com.ds.app.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvestmentResponseDTO {

    private Long id;
    private Long employeeId;
    private InvestmentType investmentType;
    private String fundName;           // resolved from MutualFundMaster if MUTUAL_FUND
    private String fundCode;
    private String securityName;       // for DIRECT_EQUITY / BONDS
    private BigDecimal declaredAmount;
    private ComplianceStatus complianceStatus;
    private String reviewedByName;     // resolved from User — Finance reviewer
    private String reviewNote;
    private LocalDateTime declaredAt;
}

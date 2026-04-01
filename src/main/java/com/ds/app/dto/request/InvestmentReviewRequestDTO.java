package com.ds.app.dto.request;

import com.ds.app.enums.ComplianceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentReviewRequestDTO {

    @NotNull(message = "Compliance status is required")
    private ComplianceStatus complianceStatus;  // COMPLIANT / NON_COMPLIANT

    @Size(max = 500)
    private String reviewNote;
    
}

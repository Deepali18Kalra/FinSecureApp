package com.ds.app.dto.request;

import com.ds.app.enums.BankStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceBankAccountRequestDTO {
    @NotBlank(message = "Bank name is required")
    @Size(max = 100)
    private String bankName;

    @NotBlank(message = "Bank code is required")
    @Size(max = 20)
    private String bankCode;

    @NotNull(message = "Status is required")
    private BankStatus status;  // WHITELISTED or BLACKLISTED

}

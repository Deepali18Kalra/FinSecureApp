package com.ds.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBankAccountRequestDTO {

    @NotNull(message = "Bank ID is required")
    private Long bankId;  // validated against BankMaster — BLACKLISTED → alert raised

    @NotBlank(message = "Account number is required")
    @Size(min = 8, max = 30)
    private String accountNumber;  // will be encrypted before saving

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC format")
    private String ifscCode;

    @NotBlank(message = "Account holder name is required")
    @Size(max = 100)
    private String accountHolderName;


}

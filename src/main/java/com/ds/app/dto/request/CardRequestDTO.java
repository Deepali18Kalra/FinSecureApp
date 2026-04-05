package com.ds.app.dto.request;

import com.ds.app.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardRequestDTO {


    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 19)
    private String cardNumber;  // encrypted before saving

    @NotNull(message = "Card type is required")
    private CardType cardType;  // CREDIT / DEBIT / PREPAID

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Format must be YYYY-MM")
    private String expiryDate;  // parsed to YearMonth in service
    
    
}

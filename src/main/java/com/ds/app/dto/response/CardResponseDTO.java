package com.ds.app.dto.response;

import com.ds.app.enums.CardStatus;
import com.ds.app.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CardResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeName;       // resolved from User
    private String cardNumberMasked;   // **** **** **** 1234
    private CardType cardType;
    private String expiryDate;         // "2027-08" format
    private CardStatus cardStatus;
    private LocalDate issuedAt;
}

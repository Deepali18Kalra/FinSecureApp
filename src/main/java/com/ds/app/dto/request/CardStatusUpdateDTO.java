package com.ds.app.dto.request;

import com.ds.app.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusUpdateDTO {


    @NotNull(message = "Card status is required")
    private CardStatus cardStatus;  // ACTIVE / BLOCKED / EXPIRED
}

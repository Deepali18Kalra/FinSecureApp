package com.ds.app.dto.request;



import com.ds.app.enums.BankValidationStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankValidationReviewRequestDTO {
	
	@NotNull
	private BankValidationStatus validationStatus; // APPROVED, REJECTED, BLACKLISTED
	
	@Size(max = 500 , message = "Comments cannot exceed 500 characters")
	private String comments;
	
	
	
	

}

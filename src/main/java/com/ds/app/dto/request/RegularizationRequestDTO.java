package com.ds.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegularizationRequestDTO {
	
	@NotNull(message = "Date is required")
	private LocalDate date;
	
	
	private LocalTime punchInTime;
	private LocalTime punchOutTime;
	
	@NotBlank(message = "Reason is required")
	private String reason;
}

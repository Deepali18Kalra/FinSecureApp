package com.ds.app.dto;

import java.time.LocalDate;
import com.ds.app.entity.LeaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaverRequest {
	
	@NotNull(message = "Start date is required")
	private LocalDate startDate;
	
	@NotNull(message = "End date is required")
	private LocalDate endDate;
	
	@NotNull(message = "Leave type is required")
	private LeaveType leaveType;
	
	@NotBlank(message = "Reason is required")
	private String reason;
}

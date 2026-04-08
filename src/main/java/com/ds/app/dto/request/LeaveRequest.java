package com.ds.app.dto.request;

import com.ds.app.enums.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {
	
	@NotNull(message = "Start date is required")
	@FutureOrPresent(message = "Start date cannot be in the past")
	private LocalDate startDate;
	
	@NotNull(message = "End date is required")
	private LocalDate endDate;
	
	@NotNull(message = "Leave type is required")
	private LeaveType leaveType;
	
	@NotBlank(message = "Reason is required")
	private String reasonForLeave;
}

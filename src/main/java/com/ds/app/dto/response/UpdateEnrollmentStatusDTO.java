package com.ds.app.dto.response;

import com.ds.app.enums.EnrollmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEnrollmentStatusDTO {
	
	@NotNull(message="Status is required")
	private EnrollmentStatus status;

}
